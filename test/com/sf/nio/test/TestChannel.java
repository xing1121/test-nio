package com.sf.nio.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

/*
 * 一、通道（Channel）：用于源节点与目标节点的连接。在 Java NIO 中负责缓冲区中数据的传输。Channel 本身不存储数据，因此需要配合缓冲区进行传输。
 * 
 * 二、通道的主要实现类
 * 	java.nio.channels.Channel 接口：
 * 		|--FileChannel
 * 		|--SocketChannel
 * 		|--ServerSocketChannel
 * 		|--DatagramChannel
 * 
 * 三、获取通道
 * 1. Java 针对支持通道的类提供了 getChannel() 方法
 * 		本地 IO：
 * 		FileInputStream/FileOutputStream
 * 		RandomAccessFile
 * 
 * 		网络IO：
 * 		Socket
 * 		ServerSocket
 * 		DatagramSocket
 * 		
 * 2. 在 JDK 1.7 中的 NIO.2 针对各个通道提供了静态方法 open()，如FileChannel.open()
 * 3. 在 JDK 1.7 中的 NIO.2 的 Files 工具类的 newByteChannel()
 * 
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 * 
 * 五、分散(Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 * 
 * 六、字符集：Charset
 * 编码：字符串 -> 字节数组
 * 解码：字节数组  -> 字符串
 * 
 * filp()方法：将缓冲区的limit设置为position，position设置为0
 */
public class TestChannel {
	
	// 6.编码和解码
	@Test
	public void test6() throws Exception{
		// 获取字符集
		Charset gbkCharset = Charset.forName("GBK");
		
		// 获取编码器，解码器
		CharsetEncoder encoder = gbkCharset.newEncoder();
		CharsetDecoder decoder = gbkCharset.newDecoder();
		
		// 创建字符缓冲区
		CharBuffer cb = CharBuffer.allocate(1024);
		
		// 字符串写入字符缓冲区
		cb.put("哈啊我啊");
		
		// 切换为读模式
		cb.flip();
		
		// 编码，得到字节缓冲区
		ByteBuffer bb = encoder.encode(cb);
		
		// 查看字节缓冲区内容
		for (int i = 0; i < bb.limit(); i++) {
			System.out.println(bb.get());
		}
		
		// 切换为读模式
		bb.flip();
		
		// 解码，得到字符缓冲区
		CharBuffer cb2 = decoder.decode(bb);
		
		// 查看字符缓冲区内容
		System.out.println(cb2.toString());
	}
	
	// 5.字符集，查看所有字符集
	@Test
	public void test5(){
		Map<String,Charset> availableCharsets = Charset.availableCharsets();
		Set<Entry<String, Charset>> entrySet = availableCharsets.entrySet();
		for (Entry<String, Charset> entry : entrySet) {
			System.out.println(entry.getKey() + "==" + entry.getValue());
		}
		System.out.println("共 " + entrySet.size() + " 字符集");
	}
	
	// 4.分散和聚集
	@Test
	public void test4() throws Exception{
		// 读取输入输出文件
		RandomAccessFile rafIn = new RandomAccessFile("1.txt", "rw");
		RandomAccessFile rafOut = new RandomAccessFile("2.txt", "rw");
		
		// 获取输入输出通道 
		FileChannel inChannel = rafIn.getChannel();
		FileChannel outChannel = rafOut.getChannel();
		
		// 创建两个缓冲区
		ByteBuffer buf1 = ByteBuffer.allocate(10);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);
		
		// 创建缓冲区数组
		ByteBuffer[] dsts = {buf1, buf2};
		
		// 分散将输入通道的数据写入两个缓冲区
		inChannel.read(dsts);
		
		// 查看缓冲区内容
		System.out.println(new String(buf1.array()));
		System.out.println("----------------------");
		System.out.println(new String(buf2.array()));
		
		// 切换缓冲区为读模式
		buf1.flip();
		buf2.flip();

		// 输出通道读取缓冲区数据
		outChannel.write(dsts);
		
		// 关闭
		outChannel.close();
		inChannel.close();
		rafOut.close();
		rafIn.close();
	}
	
	// 3.使用transferTo或transferFrom在通道间完成数据的传输（利用的还是直接缓冲区）文件的复制
	@Test
	public void test3() throws Exception{
		// 获取通道
		FileChannel inChannle = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
		
		// 通道传输数据
		inChannle.transferTo(0, inChannle.size(), outChannel);
		
		// 关闭
		outChannel.close();
		inChannle.close();
	}
	
	// 2.使用FileFileChannel.open()获取通道，使用直接缓冲区完成文件的复制
	@Test
	public void test2() throws Exception{
		// 获取输入输出通道
		FileChannel inChannle = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
		
		// 获取输入输出内存映射文件，即缓冲区
		MappedByteBuffer inMap = inChannle.map(MapMode.READ_ONLY, 0, inChannle.size());
		MappedByteBuffer outMap = outChannel.map(MapMode.READ_WRITE, 0, inChannle.size());
		
		// 直接操作缓冲区，输入文件的数据写入字节数组
		byte[] dst = new byte[inMap.limit()];
		inMap.get(dst);
		
		// 字节数组写入输出缓冲区
		outMap.put(dst);
		
		// 关闭
		outChannel.close();
		inChannle.close();
	}
	
	// 1.使用流获取通道，通过缓冲区和通道完成文件的复制
	@Test
	public void test1() throws Exception{
		// 获取输入输出流
		FileInputStream fis = new FileInputStream("1.jpg");
		FileOutputStream fos = new FileOutputStream("2.jpg");
		
		// 获取输入输出通道
		FileChannel inChannel = fis.getChannel();
		FileChannel outChannel = fos.getChannel();
		
		// 创建缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		// 输入通道的数据写入缓冲区
		while (inChannel.read(buf) != -1) {
			// 切换缓冲区为读模式
			buf.flip();
			// 输出通道读取缓冲区数据
			outChannel.write(buf);
			// 清空缓冲区
			buf.clear();
		}
		
		// 关闭
		outChannel.close();
		inChannel.close();
		fos.close();
		fis.close();
	}

}









