package chabernac.preference;

public interface iApplicationPreferenceListener {
  void applicationPreferenceChanged(String aKey, String aValue);
  void applicationPreferenceChanged(Enum anEnumValue);
}
