package com.sf.nio.test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;

import org.junit.Test;

/*
 * 管道Pipe
 */
public class TestPipe {

	@Test
	public void test1() throws Exception{
		// 创建管道
		Pipe pipe = Pipe.open();
		
		// 获取管道中的输入通道
		SinkChannel sinkChannel = pipe.sink();
		sinkChannel.configureBlocking(false);
		
		// 获取管道中的输出通道
		SourceChannel sourceChannel = pipe.source();
		sourceChannel.configureBlocking(false);
		
		// 创建缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put("abcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefg".getBytes());
		ByteBuffer buf2 = ByteBuffer.allocate(10);
		
		// 缓冲区内容写入输入管道
		buf.flip();
		sinkChannel.write(buf);
		
		// 从输出管道读取内容到缓冲区
		int len = 0;
		while ((len = sourceChannel.read(buf2)) > 0) {
			buf2.flip();
			System.out.print(new String(buf2.array(), 0, len));
			buf2.clear();
		}
		System.out.println();
		
		// 关闭
		sourceChannel.close();
		sinkChannel.close();
	}
	
}
