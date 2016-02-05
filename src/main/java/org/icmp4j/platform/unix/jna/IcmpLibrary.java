package org.icmp4j.platform.unix.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public interface IcmpLibrary extends Library {
	
	public void icmp4j_exist(PointerByReference val);
	public void icmp4j_exist_free(Pointer p);
	
	public static class Icmp4jStruct extends Structure {
		public static class ByReference extends Icmp4jStruct implements Structure.ByReference {}
		
		public String		host;
		public int			ttl;
		public int			packetSize;
		public NativeLong 	timeOut;
		
		public int			retCode;
		public int			hasTimeout;
		public int			bytes;
		public int			returnTtl;
		public int			rtt;
		public String		address;
		public String		errorMsg;
		public int			errno;
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList (new String[] {"host", "ttl", "packetSize", "timeOut", "retCode", "hasTimeout",
					"bytes", "returnTtl", "rtt", "address", "errorMsg", "errno"});
		}
	} 
	public void icmp4j_start(Icmp4jStruct.ByReference sval);
	public void icmp4j_free(Icmp4jStruct.ByReference sval);

}
