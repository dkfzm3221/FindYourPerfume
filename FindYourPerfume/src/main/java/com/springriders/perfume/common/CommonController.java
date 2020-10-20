package com.springriders.perfume.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springriders.perfume.Const;
import com.springriders.perfume.SecurityUtils;
import com.springriders.perfume.ViewRef;
import com.springriders.perfume.common.model.CommonVO;
import com.springriders.perfume.common.model.NoteCodeVO;
import com.springriders.perfume.common.model.PerfumeDMI;
import com.springriders.perfume.common.model.PerfumePARAM;
import com.springriders.perfume.user.UserService;

@Controller
@RequestMapping("/common")
public class CommonController {
	
	@Autowired
	private CommonService service;
	
	@Autowired
	private UserService uService;

	@RequestMapping(value="/main", method = RequestMethod.GET)
	public String main(PerfumePARAM param, Model model, HttpServletRequest req) {
		
		int i_user = SecurityUtils.getLoginUserPk(req);

		param.setI_user(i_user);

		List<PerfumeDMI> perfume = service.selPerfumeList(param);
				
		List<String> brandAlphabet = new ArrayList();
        char aString = 65 ;
        
        while(true){
            // 특수문자 시작시 알파벳 소문자로 고정
            if(aString == 91)
                aString = 97 ;
 
            // 아스키 코드를 문자형으로 변환
            String str = String.valueOf(aString) ;

            brandAlphabet.add(str);
  
            // 아스키값 증가
            aString++ ;
 
            // 알파벳 소문자 z가 끝날시 종료 처리
            if(aString > 90)
                break ;
        }
		
        model.addAttribute("brandAlphabet", brandAlphabet);
		model.addAttribute("perfume", perfume);
		

		model.addAttribute(Const.CSS, "main");
		model.addAttribute(Const.TITLE, "main");
		model.addAttribute(Const.VIEW, "main");
		
		return ViewRef.TEMP_MENU;
	}
	
	@RequestMapping(value="/main", method = RequestMethod.POST)
	public String main(CommonVO vo, HttpSession hs, RedirectAttributes ra) {
		int result = uService.login(vo);
		if(result == Const.SUCCESS) {
			hs.setAttribute(Const.LOGIN_USER, vo);
			ra.addFlashAttribute("data", vo);
			return "redirect:/common/main";
		}
		
		String msg = null;
		if(result == Const.NO_ID) {
			msg = "아이디를 확인해 주세요";
		}else if(result == Const.NO_PW) {
			msg = "비밀번호를 확인해 주세요";
		}else if(result == Const.EMPTY_ID) {
			msg = "아이디를 입력해 주세요";
		}

		vo.setMsg(msg);
		ra.addFlashAttribute("data", vo);
		return "redirect:/common/main";
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(HttpSession hs) {
		hs.invalidate();
		return "redirect:/common/main";
	}

	@RequestMapping("/detail")
	public String detail(PerfumePARAM param, Model model, HttpServletRequest req) {
		
		int i_user = SecurityUtils.getLoginUserPk(req);
		param.setI_user(i_user);
		param.setI_p(321);
		PerfumeDMI perfume = service.selPerfume(param);
		List<NoteCodeVO> note = service.selPerfumeNoteList(param);
		model.addAttribute("perfume", perfume);
		model.addAttribute("note", note);
		
		model.addAttribute(Const.CSS, "detail");
		model.addAttribute(Const.TITLE, "디테일 페이지"); //가게명
		model.addAttribute(Const.VIEW, "detail/perfumeDetail");
		return ViewRef.TEMP_MENU;
	}
	
	@RequestMapping("/ajaxSelBrandAlphabet")
	@ResponseBody
	public List<PerfumeDMI> ajaxSelBrandAlphabet(PerfumeDMI dm){
		System.out.println(dm.getB_nm_eng());
		return service.selBrandAlphabet(dm);
	}
	
	
}