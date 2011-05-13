package chabernac.comet;

import javax.servlet.http.HttpServletResponse;

public class ServletResponseEndPointValidator implements iEndPointValidator {
  private final HttpServletResponse myResponse;

  public ServletResponseEndPointValidator(HttpServletResponse anResponse) {
    super();
    myResponse = anResponse;
  }

  @Override
  public boolean isValid() {
    return !myResponse.isCommitted();
  }

}
