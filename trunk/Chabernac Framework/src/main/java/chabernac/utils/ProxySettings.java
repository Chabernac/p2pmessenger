package chabernac.utils;

import java.io.Serializable;

public class ProxySettings implements Serializable
{
private String proxyHost = null;
private int proxyPort = 8080;
private boolean useProxy;

public ProxySettings()
{
	initialize();
}

private void initialize()
{
	proxyHost = "";
}

public String getProxyHost(){return proxyHost;}
public int getProxyPort(){return proxyPort;}
public boolean useProxy(){return useProxy;}

public void setProxyHost(String proxyHost){this.proxyHost = proxyHost;}
public void setProxyPort(int proxyPort){this.proxyPort = proxyPort;}
public void setProxy(boolean proxy){this.useProxy = proxy;}
}


