package handlers.user;

import handlers.RequestExecutor;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

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
            CommonDbService dbService) {
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
                tryWrapMessageCallback(response, "registered", callback);
            else
                try {
                    response.getWriter().println("registered");
                } catch (IOException e) {
                    // nothing just return success status: this call does not require wrapper
                }
            return Status.SUCCESS;
        }
        msg.append("Error: User with such name already exists\n");
        trySetErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
