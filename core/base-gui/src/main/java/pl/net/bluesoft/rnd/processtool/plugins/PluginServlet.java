package pl.net.bluesoft.rnd.processtool.plugins;

import org.osgi.framework.BundleException;
import pl.net.bluesoft.rnd.processtool.plugins.osgi.PluginHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static pl.net.bluesoft.util.lang.FormatUtil.nvl;

/**
 * @author tlipski@bluesoft.net.pl
 */
public class PluginServlet extends HttpServlet {

	static PluginHelper pluginHelper;

	private static Logger LOGGER = Logger.getLogger(PluginServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (pluginHelper != null) {
			try {
				pluginHelper.stopPluginSystem();
				pluginHelper = null;
			} catch (BundleException e) {
				LOGGER.throwing("Exception while osgi stop", e.getMessage(), e);
			}
		}
		initPluginHelper();
	}

	@Override
	public void init() throws ServletException {
		LOGGER.info("init");

		initPluginHelper();
		LOGGER.info("initout");

	}

	private void initPluginHelper() throws ServletException {
		try {
			if (pluginHelper == null) {
				pluginHelper = new PluginHelper();

				ProcessToolRegistry ptcf = (ProcessToolRegistry) getServletContext()
						.getAttribute(ProcessToolRegistry.class.getName());

				pluginHelper.initializePluginSystem(
						nvl(getServletConfig().getInitParameter("osgi-plugins-directory"),
							getServletConfig().getServletContext().getRealPath("/WEB-INF/osgi")),
						ptcf);
			}
		} catch (Exception e) {
			pluginHelper = null;
			LOGGER.throwing("Exception while osgi init", e.getMessage(), e);
			throw new ServletException(e);
		}
	}

	@Override
	public void destroy() {
		LOGGER.info("destroy");
		super.destroy();
		if (pluginHelper != null) {
			try {
				pluginHelper.stopPluginSystem();
				pluginHelper = null;
			} catch (BundleException e) {
				LOGGER.throwing("Exception while osgi stop", e.getMessage(), e);				
			}
		}
	}
}
