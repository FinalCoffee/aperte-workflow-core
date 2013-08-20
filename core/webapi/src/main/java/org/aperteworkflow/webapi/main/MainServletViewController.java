package org.aperteworkflow.webapi.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.net.bluesoft.rnd.processtool.bpm.ProcessToolBpmSession;
import pl.net.bluesoft.rnd.processtool.model.UserData;
import pl.net.bluesoft.rnd.processtool.usersource.IPortalUserSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller(value = "MainServletViewController")
@RequestMapping(value ="/view")
public class MainServletViewController extends AbstractMainController<ModelAndView, HttpServletRequest>
{
    @RequestMapping()
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView modelView = new ModelAndView("index");
        modelView.addObject(IS_STANDALONE, true);

		processRequest(modelView, request);

	    return modelView;
	}

	@Override
	protected void addObject(ModelAndView modelView, String key, Object value) {
		modelView.addObject(key, value);
	}

	@Override
	protected UserData getUserByRequest(IPortalUserSource userSource, HttpServletRequest request) {
		return userSource.getUserByRequest(request);
	}

	@Override
	protected ProcessToolBpmSession getSession(HttpServletRequest request) {
		return (ProcessToolBpmSession)request.getAttribute(ProcessToolBpmSession.class.getName());
	}

	@Override
	protected void setSession(ProcessToolBpmSession bpmSession, HttpServletRequest request) {
		request.setAttribute(ProcessToolBpmSession.class.getName(), bpmSession);
	}
}
