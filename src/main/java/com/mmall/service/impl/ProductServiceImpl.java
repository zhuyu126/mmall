package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse saveOrupdateProduct(Product product){
        if(product!=null){//商品参数不为空
            //判断商品子图知否为空
            if(StringUtils.isNotBlank(product.getSubImages())){//子图不为空的，截取子图作为主图
                String[] subImagesArray=product.getSubImages().split(",");
                if (subImagesArray.length>0){
                    product.setMainImage(subImagesArray[0]);
                }
                if(product.getId()!=null){
                    int rowCount=productMapper.updateByPrimaryKeySelective(product);
                    if(rowCount>0){
                        return ServerResponse.createBySuccess("商品信息更新成功");
                    }
                    return ServerResponse.createByErrorMessage("商品信息更新失败");
                }else {
                    int rowCount=productMapper.insert(product);
                    if(rowCount>0){
                        return ServerResponse.createBySuccess("商品信息添加成功");
                    }
                    return ServerResponse.createByErrorMessage("商品信息添加失败");
                }
            }

        }
        return ServerResponse.createByErrorMessage("新增或更新商品参数不正确");
    }


    public ServerResponse<String>setSaleStatus(Integer productId,Integer status){
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccess("商品上架信息修改成功");
        }
        return ServerResponse.createByErrorMessage("商品上架信息修改失败");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null) {
            return ServerResponse.createByErrorMessage( "商品已下架或已删除" );
        }//vo对象--value object
        //pojo - bo(business object) - vo(view object)
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
        }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitel());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //parentCategoryId
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            //如果子分类为0，默认为根节点
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //从DB中拿出来的时候是一个毫秒数，不利于阅读。对时间进行一个转换
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.DateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.DateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //startPage--start
        PageHelper.startPage(pageNum,pageSize);
        //填充sql查询逻辑
        List<Product>productList=productMapper.selectList();
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //pageHelper-收集
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitel());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){//名字存在
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        //查询模糊查询
        List<Product>productList=productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
}
