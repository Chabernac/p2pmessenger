package chabernac.reflection;

public class Reflection
{
	public static Class getWrapperClass(Class aClass)
	{
		if(aClass.isPrimitive())
		{
			if(aClass.equals(Boolean.TYPE)){return Boolean.class;}
			else if(aClass.equals(Short.TYPE)){return Short.class;}
			else if(aClass.equals(Integer.TYPE)){return Integer.class;}
			else if(aClass.equals(Long.TYPE)){return Long.class;}
			else if(aClass.equals(Float.TYPE)){return Float.class;}
			else if(aClass.equals(Double.TYPE)){return Double.class;}
			else if(aClass.equals(Byte.TYPE)){return Byte.class;}
			else if(aClass.equals(Character.TYPE)){return Character.class;}
			else return aClass;
		}
		else
		{
			return aClass;
		}
	}
}
