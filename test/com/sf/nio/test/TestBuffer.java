package com.sf.nio.test;

import java.nio.ByteBuffer;

import org.junit.Test;

/*
 * 一、缓冲区（Buffer），在Java NIO中负责数据的存取。缓冲区就是数组，用于存储不同数据类型的数据。
 * 
 * 根据数据类型不同，提供了对应类型的缓冲区，boolean类型除外。
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 
 * 上述缓冲区的管理方式几乎一致，通过allocate()获取缓冲区
 * 
 * 二、缓冲区存取数据的两个核心方法：
 * put()	：	存入数据到缓冲区中
 * get()	：	获取缓冲区中的数据
 * 
 * 三、缓冲区中的四个核心属性：
 * capacity : 容量，表示缓冲区中最大存储数据的容量。一旦声明不能改变。
 * limit : 界限，表示缓冲区中可以操作数据的大小。（limit 后数据不能进行读写）
 * position : 位置，表示缓冲区中正在操作数据的位置。
 * mark : 标记，表示记录当前 position 的位置。可以通过 reset() 恢复到 mark 的位置
 * 
 * 0 <= mark <= position <= limit <= capacity
 * 
 * 四、直接缓冲区与非直接缓冲区：
 * 非直接缓冲区：通过 allocate() 方法分配缓冲区，将缓冲区建立在 JVM 的内存中
 * 直接缓冲区：通过 allocateDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 * 
 * filp()方法：将缓冲区的limit设置为position，position设置为0
 */
public class TestBuffer {
	
	@Test
	public void test3(){
		// 分配非直接缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		System.out.println(buf.isDirect());
		
		// 分配直接缓冲区
		ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);
		System.out.println(buf2.isDirect());
	}
	
	@Test
	public void test2(){
		String str = "abcde";
	
		// 1.创建字节缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		// 2.写入数据
		buf.put(str.getBytes());
		
		// 3.读取缓冲区内容
		buf.flip();
		byte[] dst = new byte[buf.limit()];
		buf.get(dst, 0, 2);//读取两个字节
		System.out.println("读取两个字节：" + new String(dst, 0, 2));
		System.out.println("现在的位置：" + buf.position());
		
		// 4.标记
		buf.mark();
		
		// 5.再获取两个字节
		buf.get(dst, 2, 2);//读取两个字节
		System.out.println("读取两个字节：" + new String(dst, 2, 2));
		System.out.println("现在的位置：" + buf.position());
		
		// 6.回退
		buf.reset();
		System.out.println("现在的位置：" + buf.position());
		
		// 7.判断缓冲区是否还有剩余的数据
		if (buf.hasRemaining()) {
			System.out.println("缓冲区剩余数据数量：" + buf.remaining());
		}
		
	}
		
	@Test
	public void test1(){
		String str = "abcde";
		
		// 1.创建字节缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		System.out.println("-----------------allocate()------------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 2.写入数据到缓冲区
		buf.put(str.getBytes());
		System.out.println("-----------------put()------------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 3.使缓冲区可以被读取
		buf.flip();
		System.out.println("-----------------flip()------------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 4.读取缓冲区内容
		byte[] dst = new byte[buf.limit()];
		buf.get(dst, 0, buf.limit());
		System.out.println("-----------------get()------------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		System.out.println("获取到的内容：" + new String(dst, 0, dst.length));
		
		// 5.位置复原，可重复读取缓冲区内容
		buf.rewind();
		System.out.println("-----------------rewind()------------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 6.清空缓冲区（数据仍在）
		buf.clear();
		System.out.println("-----------------clear()------------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		System.out.println("---" + (char)buf.get());
		
	}

}
