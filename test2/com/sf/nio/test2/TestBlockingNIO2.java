package com.sf.nio.test2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestBlockingNIO2 {
	
	// 客户端
	@Test
	public void client() throws Exception{
		// 1.获取通道
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		
		// 2.分配缓冲区
		ByteBuffer bf = ByteBuffer.allocate(1024);
		
		// 3.读取本地文件并发送到服务端
		FileChannel fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		while (fileChannel.read(bf) != -1) {//通道->写入缓冲区
			bf.flip();
			socketChannel.write(bf);//读缓冲区->通道
			bf.clear();
		}
		
		// 4.结束发送
		socketChannel.shutdownOutput();
		
		// 5.接收反馈
		int len = 0;
		while ((len = socketChannel.read(bf)) != -1) {
			bf.flip();
			System.out.println(new String(bf.array(), 0, len));
			bf.clear();
		}
		
		// 4.关闭通道
		fileChannel.close();
		socketChannel.close();
		
	}
	
	// 服务端
	@Test
	public void server() throws Exception{
		// 1.获取服务端通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		// 2.绑定端口
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		// 3.获取客户端连接的通道
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		// 4.分配缓冲区
		ByteBuffer bf = ByteBuffer.allocate(1024);
		
		// 5.循环读取，写入文件通道中
		FileChannel fileChannel = FileChannel.open(Paths.get("1-2.jpg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		while (socketChannel.read(bf) != -1) {
			bf.flip();
			fileChannel.write(bf);
			bf.clear();
		}
		
		// 6.反馈给客户端
		bf.put("服务端收到数据，这是返回给客户端的反馈！".getBytes());
		bf.flip();
		socketChannel.write(bf);
		
		// 7.关闭通道
		socketChannel.close();
		fileChannel.close();
		serverSocketChannel.close();
	}
	
}
