package io.netty.example.telnet;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.zxz.controller.RoomManager;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.service.DateServiceImpl;
import com.zxz.service.UserDroppedService;
import com.zxz.utils.Constant;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter implements Constant{
	
	private static Logger logger = Logger.getLogger(HeartbeatServerHandler.class);  
	
	// Return a unreleasable view on the given ByteBuf
	// which will just ignore release and retain calls.
	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
			.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8)); // 1

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) { // 2
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				//{"tm":5,"tp":"1","m":"xtjc"}
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put("method", "xtjc");
				outJsonObject.put("tm", WRITE_IDEL_TIME_OUT);
				ctx.write(outJsonObject);
			} else if (event.state() == IdleState.WRITER_IDLE) {
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put("method", "xtjc");
				outJsonObject.put("tm", WRITE_IDEL_TIME_OUT);
				ctx.write(outJsonObject);
			} else if (event.state() == IdleState.ALL_IDLE) {
				ctx.close();
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}