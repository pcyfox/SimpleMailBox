package com.simple.base.glidemodule;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.simple.base.config.AppConfig;

import static com.simple.base.constant.Constant.MAX_TOTAL_SIZE;


/**
 * Created by Administrator on 2017/12/22 0022.
 * 在Glide3中需要在AndroidManifest.xml中配置
 * <p>
 * Glide4，只要有@GlideModule注解就好
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    private static final String DISK_CACHE_NAME = AppConfig.GLIDE_CACHE_NAME;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);

        /**
         * 更改缓存最总文件夹名称
         *
         * 是在sdcard/Android/data/包名/cache/DISK_CACHE_NAME目录当中
         */
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, DISK_CACHE_NAME, MAX_TOTAL_SIZE));

        //TODO 根据需求可以自定义配置
        RequestOptions requestOptions = new RequestOptions();
        builder.setDefaultRequestOptions(requestOptions);
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
    }
}