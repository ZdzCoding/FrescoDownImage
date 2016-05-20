# FrescoDownImage 

http://blog.csdn.net/zhaodai11?viewmode=contents

Fresco是Facebook发布的开源的图片加载框架，堪称目前最好用的图片加载库，使用简单。
中文文档地址   http://fresco-cn.org/docs/index.html。
github地址：https://github.com/facebook/fresco。

因为要做图片下载功能，但是网上使用Fresco做图片下载的资料很少，特在此做一个总结一下。

Fresco使用的是三级缓存。1. Bitmap缓存  2. 未解码图片的内存缓存  3. 文件缓存。
按照正常逻辑 在图片显示出来后，才能下载。所以图片已经被请求过一次，这里没有必要在发网络请求重新去下载图片。可以直接去取缓存中图片。

具体思路：
1.判断图片是否被缓存到磁盘，如果已经缓存，直接拷贝一份，到图片下载目录。
2.如果没有缓存到磁盘，就去获取缓存中的bitmap，然后直接保存到本地。

Fresco 本地缓存路径可以通过setMainDiskCacheConfig()配置。默认的缓存路径 "/Android/data/" + PackageName + "/cache/imagepipeline_cache"目录下，最大缓存空间为300MB.

Fresco可以根据图片url获取到磁盘缓存CacheKey,方法：

```
CacheKey cacheKey =DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(Uri.parse(picUrl)));
```

通过CacheKey查找磁盘中有没有缓存文件：如果配置了

```
public static File getCachedImageOnDisk(CacheKey cacheKey) {
        File localFile = null;
        if (cacheKey != null) {
            if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;
    }
```

如果返回的localFile不为空的话，将文件拷贝到相应下载目录，重命名就可以了。

如果返回的localFile为空的话，去获取缓存中的bitmap，并保存到下载目录即可。
```
ImageRequest imageRequest = ImageRequestBuilder
        .newBuilderWithSource(Uri.parse(picUrl))
        .setProgressiveRenderingEnabled(true)
        .build();

ImagePipeline imagePipeline = Fresco.getImagePipeline();
DataSource<CloseableReference<CloseableImage>>
        dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);

dataSource.subscribe(new BaseBitmapDataSubscriber() {
    @Override
    public void onNewResultImpl(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(context, "保存图片失败啦,无法下载图片", Toast.LENGTH_SHORT).show();
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "Coderfun");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = desc + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailureImpl(DataSource dataSource) {
    }
}, CallerThreadExecutor.getInstance());
```

相关代码上传到github :
https://github.com/zhaodaizheng/FrescoDownImage

有问题欢迎大家指教，感谢~
