package acal_lab04.Hw1

import chisel3._
import acal_lab04.Lab._

class MixAdder (n:Int) extends Module{
  val io = IO(new Bundle{
      val Cin = Input(UInt(1.W))
      val in1 = Input(UInt((4*n).W))
      val in2 = Input(UInt((4*n).W))
      val Sum = Output(UInt((4*n).W))
      val Cout = Output(UInt(1.W))
  })
  //please implement your code below
  io.Sum := 0.U
  io.Cout := 0.U
  
  val FA_Array = Array.fill(n)(Module(new CLAdder()).io)
  val carry = Wire(Vec(n+1, UInt(1.W)))
  val sum   = Wire(Vec(4*n, Bool()))

  carry(0) := io.Cin

  for (i <- 0 until n) {
    FA_Array(i).in1 := io.in1((4*(i+1)-1), 4*i)//0~3 4~7....
    FA_Array(i).in2 := io.in2((4*(i+1)-1), 4*i)
    FA_Array(i).Cin := carry(i)
    carry(i+1) := FA_Array(i).Cout
    for (j <- 0 until 4){
		sum(4*i+j) := FA_Array(i).Sum(j)
    }
  }
  
  io.Sum := sum.asUInt
  io.Cout := carry(n)
}