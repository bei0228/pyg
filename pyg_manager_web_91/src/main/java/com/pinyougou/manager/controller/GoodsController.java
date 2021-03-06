package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage/{page}/{rows}")
	public PageResult  findPage(@PathVariable("page") int page, @PathVariable("rows")int rows){
		TbGoods goods = new TbGoods();
		goods.setAuditStatus("0");
		return goodsService.findPage(goods, page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取当前登录的商家的id，设置到商品表
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getTbGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbGoods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbGoods findOne(@PathVariable("id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete/{ids}")
	public Result delete(@PathVariable("ids") Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search/{page}/{rows}")
	public PageResult search(@RequestBody TbGoods goods, @PathVariable("page") int page,  @PathVariable("rows") int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/updateAuditStatus/{auditStatus}/{selectIds}")
	public Result updateAuditStatus(@PathVariable("auditStatus") String auditStatus, @PathVariable("selectIds") Long[] selectIds){
		try {
			goodsService.updateAuditStatus(auditStatus, selectIds);
			return new Result(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "审核失败");
		}
	}

	@RequestMapping("/deleteGoods/{selectIds}")
	public Result deleteGoods(@PathVariable("selectIds") Long[] selectIds){
		try {
			goodsService.deleteGoods(selectIds);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	@Reference
	private ItemPageService itemPageService;

	/**
	 * 根据goodsId生成静态页
	 * @param goodsId
	 * @return
	 */
	@RequestMapping("/generateHtml/{goodsId}")
	public Result generateHtml(@PathVariable("goodsId") Long goodsId){
		try {
			itemPageService.generateHtml(goodsId);
			return new Result(true, "页面静态化成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "页面静态化失败");
		}
	}

	/**
	 * 查询所有有效的（isDelete=0）通过审核(2=auditStatus)的商品生成静态页
	 * @return
	 */
	@RequestMapping("/generateHtmlAll")
	public Result generateHtmlAll(){
		try {
			itemPageService.generateHtmlAll();
			return new Result(true, "所有页面静态化成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "所有页面静态化失败");
		}
	}
}
