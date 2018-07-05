package com.sf.nio.test2;

import java.nio.ByteBuffer;

import org.junit.Test;

public class TestBuffer {

	@Test
	public void test2(){
		// 分配直接缓冲区
		ByteBuffer bf = ByteBuffer.allocateDirect(1024);
		System.out.println(bf.isDirect());
	}
	
	@Test
	public void test1(){
		// 分配缓冲区
		ByteBuffer bf = ByteBuffer.allocate(1024);
		System.out.println("---------------------------allocate----------------------------");
		System.out.println(bf.capacity());		//总容量
		System.out.println(bf.limit());			//限制
		System.out.println(bf.position());		//游标位置
		
		// 字节放入缓冲区
		bf.put("qwerty".getBytes());
		System.out.println("---------------------------put qwert----------------------------");
		
		// 切换模式为读
		bf.flip();// limit = position; position = 0; mark = -1;
		System.out.println("---------------------------flip----------------------------");
		
		// 读取缓冲区内容2个字节
		byte[] dst = new byte[bf.limit()];
		bf.get(dst, 0, 2);// 读取bf两个字节放入dst的0、1位置
		System.out.println("---------------------------get 0 2----------------------------");
		System.out.println(new String(dst));
		
		// 标记
		bf.mark();
		System.out.println("---------------------------mark----------------------------");
		
		// 继续读两个
		bf.get(dst, 0, 2);
		System.out.println("---------------------------get 0 2----------------------------");
		System.out.println(new String(dst));
		
		// 回退到标记位置
		bf.reset();
		System.out.println("---------------------------reset----------------------------");
		
		// 读两个
		bf.get(dst, 0, 2);
		System.out.println("---------------------------get 0 2----------------------------");
		System.out.println(new String(dst));
		
		// 复位游标
		bf.rewind();//position = 0; mark = -1;
		System.out.println("---------------------------rewind----------------------------");
		
		// 读两个
		bf.get(dst, 0, 2);
		System.out.println("---------------------------get 0 2----------------------------");
		System.out.println(new String(dst));
		
		// 清空缓冲区，原来的数据仍在，只是处于被遗忘的状态
		bf.clear();// position = 0; limit = capacity; mark = -1;
		System.out.println("---------------------------clear----------------------------");
		
		// 读第一个字节
		System.out.println((char)bf.get());
		System.out.println("---------------------------get----------------------------");
	}
	
}
