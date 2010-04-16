package chabernac.data;

public interface ObjectStringConvertor{
	public Object convertToObject(String aString) throws ConvertionException;
	public String convertToString(Object aObject) throws ConvertionException;
}