package com.luoyangwei.localclient.data.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.luoyangwei.localclient.data.model.Image;

import java.util.List;

@Dao
public interface ImageRepository {

    /**
     * 插入图片
     *
     * @param entity 图片
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Image entity);

    /**
     * 查询图片
     *
     * @param id 图片id
     * @return 图片
     */
    @Query("SELECT * FROM images WHERE id = :id")
    Image findById(Long id);

    /**
     * 查询图片
     *
     * @param resourceId 资源id
     * @return 图片
     */
    @Query("SELECT * FROM images WHERE resource_id = :resourceId")
    Image findByResourcesId(String resourceId);

    /**
     * 查询全部
     *
     * @return 全部图片
     */
    @Query("SELECT * FROM images")
    List<Image> find();

    /**
     * 查询未生成缩略图的图片
     *
     * @return 未生成缩略图的图片
     */
    @Query("SELECT * FROM images WHERE is_has_thumbnail = 0")
    List<Image> findNotHasThumbnail();

    /**
     * 查询已生成缩略图的图片
     *
     * @return 已生成缩略图的图片
     */
    @Query("SELECT * FROM images WHERE is_has_thumbnail = 1")
    List<Image> findHasThumbnail();

    /**
     * 更新图片
     *
     * @param image 图片
     */
    @Update(entity = Image.class)
    void update(Image image);

    /**
     * 删除图片
     *
     * @param image 图片
     */
    @Delete(entity = Image.class)
    void delete(Image image);
}
