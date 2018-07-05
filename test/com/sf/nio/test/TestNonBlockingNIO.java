package com.sf.nio.test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/*
 * 一、使用 NIO 完成网络通信的三个核心：
 * 
 * 1. 通道（Channel）：负责连接
 * 		
 * 	   java.nio.channels.Channel 接口：
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 * 
 * 2. 缓冲区（Buffer）：负责数据的存取
 * 
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 * 
 */
public class TestNonBlockingNIO {

	//客户端
	@Test
	public void client() throws Exception{
		// 1.获取通道
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		
		// 2.设置为非阻塞
		sChannel.configureBlocking(false);
		
		// 3.创建缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//4. 发送数据给服务端
		Scanner scan = new Scanner(System.in);
		while (scan.hasNext()) {
			String str = scan.next();
			buf.put((LocalDateTime.now() + "\n" + str).getBytes());
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		
		// 5.关闭通道
		scan.close();
		sChannel.close();
	}
	
	// 服务端
	@Test
	public void server() throws Exception{
		// 1.获取通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		// 2.设置为非阻塞
		serverSocketChannel.configureBlocking(false);
		
		// 3.监听端口
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		// 4.创建选择器
		Selector selector = Selector.open();
		
		// 5.通道注册到选择器（指定监听“接收”事件）
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		// 6.轮询判断选择器中的事件是否有发生的
		while (selector.select() > 0) {
			
			// 7.获取所有已经发生的事件
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			
			while (iterator.hasNext()) {
				// 8.获取当前事件
				SelectionKey selectionKey = iterator.next();
				
				// 9.判断是什么事件
				if (selectionKey.isAcceptable()) {
					
					// 10.如果是“接收就绪”事件，
					SocketChannel sChannel = serverSocketChannel.accept();

					// 11.设置为非阻塞
					sChannel.configureBlocking(false);
					
					// 12.注册“读就绪”事件到选择器上
					sChannel.register(selector, SelectionKey.OP_READ);
					
				} else if (selectionKey.isReadable()) {
					
					// 13.如果是"读"事件，获取注册该事件的通道，即上面的sChannel即客户端连接通道
					SocketChannel channel = (SocketChannel) selectionKey.channel();
						
					// 14.读取数据
					ByteBuffer buf = ByteBuffer.allocate(1024);
					int len = 0;
					while ((len = channel.read(buf)) > 0) {
						buf.flip();
						System.out.println(new String(buf.array(), 0, len));
						buf.clear();
					}
					
				}
				// 15.移除已经完成的事件
				iterator.remove();
			}
		}
	}
}
