package com.joyhong.cms;

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

import com.joyhong.model.Device;
import com.joyhong.model.Version;
import com.joyhong.model.User;
import com.joyhong.service.DeviceService;
import com.joyhong.service.VersionService;
import com.joyhong.service.common.FuncService;

@Controller
@RequestMapping("cms/version")
public class VersionCtrl {
	
	@Autowired
	private VersionService versionService;
	
	@Autowired
	private FuncService funcService;
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(){
		return "redirect:/cms/version/select/1";
	}
	
	@RequestMapping(value="/select/{page}", method=RequestMethod.GET)
	public String select(
			Model model,  
			@PathVariable(value="page") Integer page,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		int pageSize = 20;
		int totalRecord = versionService.selectCount();
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Version> version = versionService.selectOffsetAndLimit(offset, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("version", version);
		
		return "VersionView";
	}
	
	@RequestMapping(value="/insert", method=RequestMethod.GET)
	public String insert(
			Model model,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		model.addAttribute("version", new Version());

		return "VersionView";
	}
	
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public String insert(
			Model model, 
			@ModelAttribute("version") Version version, 
			@RequestParam("referer") String referer,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		if( versionService.insert(version) == 1 ){
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/version/select";
		}
		
		return "VersionView";
	}
	
	@RequestMapping(value="/update/{version_id}", method=RequestMethod.GET)
	public String update(
			Model model, 
			@PathVariable("version_id") Integer version_id,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		Version version = versionService.selectByPrimaryKey(version_id);
		if( version != null ){
			model.addAttribute("version", version);
			
//			List<Device> device = deviceService.selectByVersionId(version.getId());
//			model.addAttribute("device", device);
//			model.addAttribute("deviceTotal", device.size());
		
			return "VersionView";
		}
		return "redirect:/cms/version/select";
	}
	
	@RequestMapping(value="/update/{version_id}", method=RequestMethod.POST)
	public String update(
			Model model, 
			HttpSession httpSession, 
			HttpServletRequest request, 
			@PathVariable("version_id") Integer version_id, 
			@ModelAttribute("version") Version version, 
			@RequestParam("referer") String referer,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		version.setId(version_id);
		version.setModifyDate(new Date());
		if( versionService.updateByPrimaryKeyWithBLOBs(version) == 1 ){
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/version/select";
		}
		
		return "VersionView";
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(
			Model model, 
			@RequestParam("version_id") Integer version_id,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		Version version = versionService.selectByPrimaryKey(version_id);
		if( version != null ){
			version.setModifyDate(new Date());
			version.setDeleted(1);
			versionService.updateByPrimaryKey(version);
		}
		
		return "redirect:/cms/version/select";
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		//判断是否登录
		User user = (User)httpSession.getAttribute("user");
		if( user == null ){
			model.addAttribute("redirect", "redirect:/cms/user/login");
			return;
		}else{
			model.addAttribute("redirect", null);
		}
		
		//解析出方法名称
		String urlStr = request.getRequestURL().toString();
		String method = urlStr.substring(urlStr.lastIndexOf("/")+1);
		if( funcService.isNumeric(method) ){
			Integer number = Integer.valueOf(method);
			urlStr = urlStr.substring(0, urlStr.lastIndexOf("/"));
			method = urlStr.substring(urlStr.lastIndexOf("/")+1);
			if( method.equals("update") ){
				model.addAttribute("version", versionService.selectByPrimaryKey(number));
			}
		}
		model.addAttribute("method", method);
		
		//当前登录用户名
		model.addAttribute("user_nickname", user.getNickname());
		
		//返回的url地址
		model.addAttribute("referer", request.getHeader("referer"));
	}
}
