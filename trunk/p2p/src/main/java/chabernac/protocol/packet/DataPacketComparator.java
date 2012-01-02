package chabernac.protocol.packet;

import java.util.Comparator;

import chabernac.tools.StringTools;

public class DataPacketComparator implements Comparator<DataPacket> {

  @Override
  public int compare(DataPacket aDataPacket1, DataPacket aDataPacket2) {
    if(StringTools.isNumeric(aDataPacket1.getId()) && StringTools.isNumeric(aDataPacket2.getId())){
      return (int)(Long.parseLong(aDataPacket1.getId()) - Long.parseLong(aDataPacket2.getId()));
    }
    return aDataPacket1.getId().compareTo(aDataPacket2.getId());
  }

}
