package acal_lab04.Hw3

import chisel3._
import chisel3.util._
import scala.annotation.switch

//------------------Radix 4---------------------
class Booth_Mul(width:Int) extends Module {
  val io = IO(new Bundle{
    val in1 = Input(UInt(width.W))      //Multiplicand
    val in2 = Input(UInt(width.W))      //Multiplier
    val out = Output(UInt((2*width).W)) //product
  })
  //please implement your code below
	val regs = Wire(Vec(width/2, SInt((2*width).W))) //partial product regs [7:0]
	val B = Cat(io.in2,0.U(1.W)) //cat 0 在末端幫助 radix-4 編碼 B[16:0]
  //operation
  //Q1.用多個for迴圈會帶來甚麼影響嗎?
  //partial product
  for(i <- 0 to (width-1) by 2){ //(0 to 15 by 2) i = [0, 2, 4, 6, 8, 10, 12, 14]
	regs(i/2) := MuxLookup((B(i+2,i)).asUInt,0.S((2*width).W),Seq(
											//先 shift 被乘數，再乘以該項權重
											"b001".U -> (io.in1.asSInt << i),//1
											"b010".U -> (io.in1.asSInt << i),//1
											"b011".U -> ((io.in1.asSInt << i) << 1),//2
											"b100".U -> ((~(io.in1 << i) + 1.U).asSInt << 1),//-2
											"b101".U -> (~(io.in1 << i)+ 1.U).asSInt,//-1
											"b110".U -> (~(io.in1<< i)+ 1.U).asSInt,//-1
										))
  }
	
  io.out := regs.reduce((a,b) => a + b).asUInt
  
}

