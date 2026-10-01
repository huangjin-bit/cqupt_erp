package com.cqupt.mislab.erpn.dao.teacher.base;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/9/9 17:04
 */

/**
 *
 *
 * BaseDao 这个接口中提供了一些抽象的方法,抽象简单的CRUD方法
 * 必须紧记配置文件中的命名跟 方法名要相同 (约定优于配置)
 *
 * @author 何海源
 * 2014-8-8 下午4:11:22
 *
 * @version 1.0.0
 *
 */
public interface BaseDao  {
    /**
     *
     * add 添加对象 (在配置文件中的方法id必须是叫 add 否则无法添加成功)
     * @param clz
     * @param obj
     *void
     * @exception
     * @since  1.0.0
     */
    <T> void add(Class<T> clz,Object obj);
    /**
     *
     * delete 简单删除 (在配置文件中的方法id必须是叫 delete 否则无法删除成功)  </br>
     * 例如 delete(User.Class,id);
     * 		那么 配置文件中的就是
     * 			<delete id="delete" parameterType="int">
     *				delete from t_user where id=#{id}
     * 			</delete>
     *
     *
     * @param entityClass 实体类对象
     * @param obj 根据obj来筛选
     *void
     * @exception
     * @since  1.0.0
     */
    <T> void delete(Class<T> entityClass,Object obj);

    /**
     * update 更新操作 (在配置文件中的方法id必须是叫update  否则无法更新成功)
     * @param o 被更新的对象
     *void
     * @exception
     * @since  1.0.0
     */
    void update(Object o);


    /**
     *
     * load 根据单个条件直接筛选所需要的对象 (在配置文件中的方法id必须是叫 load 否则无法删除成功) 		</br>
     * 例如 load(User.class,id); 则配置文件中就写成 										</br>
     *		select id="load" parameterType="int" resultType="com.xxx.xxx.User"> </br>
     *   		select name,password from user where id=#{id}					</br>
     *	 	/select>															</br>
     * @param entityClass 实体的对象
     * @param object 筛选的内容
     * @return
     *T 返回的泛型对象
     * @exception
     * @since  1.0.0
     */
    <T>T load(Class<T> entityClass,Object object);

    /**
     *
     * findAll 找出某个实体的所有记录 (在配置文件中的方法id必须是叫 find_all  否则无法查找成功)
     * 方法的使用说明同上!
     *
     * @param entityClass 实体对象的使用
     * @return
     *List<T>
     * @exception
     * @since  1.0.0
     */
    <T> List<T> findAll(Class<T> entityClass,String methodName);

}