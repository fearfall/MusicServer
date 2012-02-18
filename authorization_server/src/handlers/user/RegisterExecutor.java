package handlers.user;

import handlers.RequestExecutor;
import org.mortbay.jetty.Request;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import handlers.user.RegisterHandler.ActionType;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/13/12
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterExecutor extends RequestExecutor {
    @Override
    public Status execute(
            HttpServletResponse response,
            Map<String, String> parameters,
            CommonDbService dbService) throws IOException {
        StringBuilder msg = new StringBuilder();
        String username = checkParameter("username", parameters, msg);
        if (username == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String pwd = checkParameter("pwd", parameters, msg);
        if (pwd == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        if (username.equals("") || pwd.equals("") ) {
            msg.append("Error: Username or password is not defined \n");
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        }
        boolean success = dbService.getUserService().registerUser(username, pwd);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            String callback = parameters.get("callback");
            if (callback != null)
                wrapMessageCallback(response, "registered", callback);
            else
                response.getWriter().println("registered");
            return Status.SUCCESS;
        }
        msg.append("Error: User with such name already exists\n");
        setErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
