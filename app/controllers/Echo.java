package controllers;

import java.io.OutputStream;
import java.util.Map;

import mashup.fm.oauth.provider.MashupOAuthProvider;
import mashup.fm.oauth.provider.util.MashupOAuthUtil;
import models.OAuthAccessor;
import net.oauth.OAuthMessage;
import play.mvc.Controller;

public class Echo extends Controller {
	
	public static void echo() throws Exception {
		OAuthMessage requestMessage = MashupOAuthUtil.getMessage(request, null);
        OAuthAccessor accessor = MashupOAuthProvider.getAccessor(requestMessage);
        MashupOAuthProvider.VALIDATOR.validateMessage(requestMessage, accessor);
        response.contentType = "text/plain";
        OutputStream out = response.out;
        out.write(("[Your UserId:" + accessor.consumer.user.toString() + "]").getBytes());
        for (Object item : request.args.entrySet()) {
            Map.Entry parameter = (Map.Entry) item;
            String[] values = (String[]) parameter.getValue();
            for (String value : values) {
                out.write((parameter.getKey() + ": " + value).getBytes());
            }
        }
        out.close();
	}

}
