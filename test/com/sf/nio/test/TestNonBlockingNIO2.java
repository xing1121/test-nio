package com.sf.nio.test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/*
 * UDP传输数据
 */
public class TestNonBlockingNIO2 {

	@Test
	public void send() throws Exception{
		// 获取通道
		DatagramChannel datagramChannel = DatagramChannel.open();
			
		// 设置为非阻塞
		datagramChannel.configureBlocking(false);
		
		// 创建缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		// 发送数据
		Scanner scan = new Scanner(System.in);
		while (scan.hasNext()) {
			String str = scan.next();
			buf.put((LocalDateTime.now() + "\n" + "二炮：" + str).getBytes());
			buf.flip();
			datagramChannel.send(buf, new InetSocketAddress("127.0.0.1", 9898));
			buf.clear();
		}
		
		// 关闭
		scan.close();
		datagramChannel.close();
	}
	
	@Test
	public void accept() throws Exception{
		// 获取通道
		DatagramChannel datagramChannel = DatagramChannel.open();
		
		// 设置为非阻塞
		datagramChannel.configureBlocking(false);
		
		// 监听端口
		datagramChannel.bind(new InetSocketAddress(9898));
		
		// 创建选择器
		Selector selector = Selector.open();
		
		// 注册通道到选择器，绑定“读就绪”事件
		datagramChannel.register(selector, SelectionKey.OP_READ);
		
		// 轮询
		while (selector.select() > 0) {
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey sk = it.next();
				
				// “读就绪”事件 
				if (sk.isReadable()) {
					
					// 创建缓冲区
					ByteBuffer buf = ByteBuffer.allocate(1024);
					
					// 通道数据写入缓冲区
					datagramChannel.receive(buf);
					
					buf.flip();
					System.out.println(new String(buf.array(), 0, buf.limit()));
					buf.clear();
				}
				
				// 移除该事件
				it.remove();
			}
		}
	}
	
}
