package com.joyhong.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Category;
import com.joyhong.service.CategoryService;
import com.joyhong.service.OrderService;
import com.joyhong.service.common.FuncService;

@Controller
@RequestMapping("cms/category")
public class CategoryCtrl {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private FuncService funcService;
	
	@RequestMapping(value="select/sample", method=RequestMethod.GET)
	public String selectSample(){
		return "redirect:/cms/category/select/sample/1";
	}
	
	@RequestMapping(value="select/order", method=RequestMethod.GET)
	public String selectOrder(){
		return "redirect:/cms/category/select/order/1";
	}
	
	@RequestMapping(value="select/sample/{page}", method=RequestMethod.GET)
	public String selectSample(
			Model model,  
			@PathVariable(value="page") Integer page
	){
		pager(model, page, "sample");
		
		return "CategoryView";
	}
	
	@RequestMapping(value="select/order/{page}", method=RequestMethod.GET)
	public String selectOrder(
			Model model,  
			@PathVariable(value="page") Integer page
	){
		pager(model, page, "order");
		
		return "CategoryView";
	}
	
	private void pager(Model model, Integer page, String type){
		int pageSize = 20;
		int totalRecord = categoryService.selectCount(type);
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Category> category = categoryService.selectOffsetAndLimit(type, offset, pageSize);
		
		List<Integer> orderCount = new ArrayList<Integer>();
		for(Category c : category){
			orderCount.add(orderService.selectCountByCategoryId(c.getId()));
		}
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("category", category);
		model.addAttribute("orderCount", orderCount);
		model.addAttribute("type", type);
	}
	
	@RequestMapping(value="insert/sample", method=RequestMethod.GET)
	public String insertSample(
			Model model
	){
		return this.insert(model, "sample");
	}
	
	@RequestMapping(value="insert/order", method=RequestMethod.GET)
	public String insertOrder(
			Model model
	){
		return this.insert(model, "order");
	}
	
	private String insert(Model model, String type){
		model.addAttribute("category", new Category());
		model.addAttribute("type", type);

		return "CategoryView";
	}
	
	@RequestMapping(value="insert/sample", method=RequestMethod.POST)
	public String insertSample(
			Model model, 
			@ModelAttribute("category") Category category, 
			@RequestParam("referer") String referer
	){
		return this.insert(category, referer);
	}
	
	@RequestMapping(value="insert/order", method=RequestMethod.POST)
	public String insertOrder(
			Model model, 
			@ModelAttribute("category") Category category, 
			@RequestParam("referer") String referer
	){
		return this.insert(category, referer);
	}
	
	private String insert(Category category, String referer){
		if( categoryService.insert(category) == 1 ){
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/category/select/"+category.getType();
		}
		
		return "CategoryView";
	}
	
	@RequestMapping(value="update/sample/{category_id}", method=RequestMethod.GET)
	public String updateSample(
			Model model, 
			@PathVariable("category_id") Integer category_id
	){
		return this.update(model, "sample", category_id);
	}
	
	@RequestMapping(value="update/order/{category_id}", method=RequestMethod.GET)
	public String updateOrder(
			Model model, 
			@PathVariable("category_id") Integer category_id
	){
		return this.update(model, "order", category_id);
	}
	
	private String update(Model model, String type, Integer category_id){
		Category category = categoryService.selectByPrimaryKey(category_id);
		if( category != null ){
			model.addAttribute("category", category);
			model.addAttribute("type", type);
		
			return "CategoryView";
		}
		return "redirect:/cms/dashboard/select";
	}
	
	@RequestMapping(value="update/sample/{category_id}", method=RequestMethod.POST)
	public String updateSample(
			Model model, 
			@PathVariable("category_id") Integer category_id,
			@ModelAttribute("category") Category category, 
			@RequestParam("referer") String referer
	){
		return this.update(category_id, category, referer);
	}
	
	@RequestMapping(value="update/order/{category_id}", method=RequestMethod.POST)
	public String updateOrder(
			Model model, 
			@PathVariable("category_id") Integer category_id,
			@ModelAttribute("category") Category category, 
			@RequestParam("referer") String referer
	){
		return this.update(category_id, category, referer);
	}
	
	private String update(Integer category_id, Category category, String referer){
		category.setId(category_id);
		if( categoryService.updateByPrimaryKeySelective(category) == 1 ){
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/category/select/"+category.getType();
		}
		
		return "CategoryView";
	}
	
	@RequestMapping(value="delete", method=RequestMethod.POST)
	public String delete(
			Model model, 
			@RequestParam("category_id") Integer category_id
	){
		Category category = categoryService.selectByPrimaryKey(category_id);
		if( category != null ){
			category.setModifyDate(new Date());
			category.setDeleted(1);
			categoryService.updateByPrimaryKey(category);
		}
		
		return "redirect:/cms/category/select/"+category.getType();
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		funcService.modelAttribute(model, httpSession, request);
	}
}
