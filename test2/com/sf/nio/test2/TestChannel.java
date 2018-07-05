package com.sf.nio.test2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Test;

public class TestChannel {
	
	// 6.编码和解码
	@Test
	public void test6(){
		// 获取字符集
		Charset gbkCharset = Charset.forName("GBK");
		
		// 编码
		ByteBuffer bf = gbkCharset.encode("哈哈哈");
		
		// 查看缓冲区内容
		for (int i = 0; i < bf.limit(); i++) {
			System.out.println(bf.get());
		}
		
		// 重置位置
		bf.rewind();
		
		// 解码
		System.out.println(gbkCharset.decode(bf));
	}
	
	// 5.查看所有字符集
	@Test
	public void test5(){
		SortedMap<String, Charset> map = Charset.availableCharsets();
		Set<Entry<String, Charset>> entrySet = map.entrySet();
		for (Entry<String, Charset> entry : entrySet) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
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
		// 获取输入输出通道
		FileChannel inputChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outputChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.READ);

		// 通道直接传输
		inputChannel.transferTo(0, inputChannel.size(), outputChannel);
		
		// 关闭
		outputChannel.close();
		inputChannel.close();
	}

	// 2.使用FileChannel.open()获取通道，使用直接缓冲区完成文件的复制
	@Test
	public void test2() throws Exception{
		// 获取输入输出通道
		FileChannel inputChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outputChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.READ);
		
		// 获取输入输出内存映射文件，即缓冲区
		MappedByteBuffer inputBf = inputChannel.map(MapMode.READ_ONLY, 0, inputChannel.size());
		MappedByteBuffer outputBf = outputChannel.map(MapMode.READ_WRITE, 0, inputChannel.size());
		
		// 输入缓冲区的内容写入输出缓冲区
		byte[] dst = new byte[inputBf.limit()];
		inputBf.get(dst);
		outputBf.put(dst);
		
		// 关闭
		outputChannel.close();
		inputChannel.close();
		
	}
	
	// 1.使用流获取通道，通过缓冲区完成文件的复制
	@Test
	public void test1() throws Exception{
		// 获取输入输出流
		FileInputStream fis = new FileInputStream("1.jpg");
		FileOutputStream fos = new FileOutputStream("2.jpg");
		
		// 获取输入输出通道
		FileChannel inputChannel = fis.getChannel();
		FileChannel outputChannel = fos.getChannel();
		
		// 分配缓冲区
		ByteBuffer bf = ByteBuffer.allocate(1024);
		
		// 循环，缓冲区读取输入通道数据写入输出通道
		while (inputChannel.read(bf) != -1) {
			bf.flip();
			outputChannel.write(bf);
			bf.clear();
		}
		
		// 关闭
		outputChannel.close();
		inputChannel.close();
		fos.close();
		fis.close();
	}
	
}
