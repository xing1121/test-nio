package com.sf.nio2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
 * 非阻塞只能用于网络通信，无法用于FileChannel
 */
public class TestBlockingNIO2 {
	
	// 客户端向服务端发送图片，服务端接收后保存到本地，并给客户端反馈信息
	@Test
	public void client() throws Exception{
		// 1.获取通道
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		FileChannel fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		
		// 2.分配缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		// 3.读取本地文件并发送到服务端
		while(fileChannel.read(buf) != -1){
			buf.flip();
			socketChannel.write(buf);
			buf.clear();
		}
		
		// 4.结束向服务端发送数据
		socketChannel.shutdownOutput();
		
		// 5.接收服务端的反馈
		int len = 0;
		while ((len = socketChannel.read(buf)) != -1) {
			buf.flip();
			System.out.println(new String(buf.array(), 0, len));
			buf.clear();
		}
		
		// 6.关闭通道
		fileChannel.close();
		socketChannel.close();
	}
	
	// 服务端
	@Test
	public void server() throws Exception{
		// 1.获取通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		FileChannel fileChannel = FileChannel.open(Paths.get("1-1.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		
		// 2.绑定连接（端口号）
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		// 3.获取客户端连接的通道
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		// 4.分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		// 5.接收客户端数据并保存到本地
		while (socketChannel.read(buf) != -1) {
			buf.flip();
			fileChannel.write(buf);
			buf.clear();
		}
		
		// 6.反馈给客户端
		buf.put("服务端收到数据，这是给客户端的反馈！".getBytes());
		buf.flip();
		socketChannel.write(buf);
		
		// 7.关闭通道
		socketChannel.close();
		fileChannel.close();
		serverSocketChannel.close();
		
	}
	
}
