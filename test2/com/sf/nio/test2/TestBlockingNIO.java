package com.sf.nio.test2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestBlockingNIO {

	// 客户端，向服务端发送数据
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
		
		// 4.关闭通道
		fileChannel.close();
		socketChannel.close();
	}
	
	// 服务端，接收图片并保存
	@Test
	public void server() throws Exception{
		// 1.获取通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		// 2.绑定连接（端口号）
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		// 3.获取客户端连接的通道
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		// 4.分配指定大小的缓冲区
		ByteBuffer bf = ByteBuffer.allocate(1024);
		
		// 5.接收客户端数据并保存到本地
		FileChannel fileChannel = FileChannel.open(Paths.get("1-1.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		while(socketChannel.read(bf) != -1){//通道->写入缓冲区
			bf.flip();
			fileChannel.write(bf);//读缓冲区->通道
			bf.clear();
		}
		
		// 6.关闭通道
		fileChannel.close();
		socketChannel.close();
		serverSocketChannel.close();
	}
	
	
}
