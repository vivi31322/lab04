package acal_lab04.Hw2

import chisel3._
import chisel3.util._
import acal_lab04.Lab._

class Add_Suber extends Module{
  val io = IO(new Bundle{
  val in_1 = Input(UInt(4.W))
	val in_2 = Input(UInt(4.W))
	val op = Input(Bool()) // 0:ADD 1:SUB
	val out = Output(UInt(4.W))
	val o_f = Output(Bool())
  })

  //please implement your code below
  
  val FA_Array = Array.fill(4)(Module(new FullAdder()).io)//Full adder array
  val sum = Wire(Vec(4,Bool()))//sum暫存
  val carry = Wire(Vec(5,Bool()))//carry暫存
  val io_in2 = Wire(UInt(4.W))//io.in2 暫存, 之後替換成二補數或維持原樣
  io_in2 := Mux(io.op,((~io.in_2)+1.U(4.W)),io.in_2)//if op = SUB, io_in2 = 2', or io_in2 = original input
  
  io.out := 0.U
  io.o_f := false.B
  carry(0):= false.B
  
  for( i<- 0 until 4){
	FA_Array(i).A := io.in_1(i)
	FA_Array(i).B := io_in2(i)
	FA_Array(i).Cin := carry(i)
	sum(i) := FA_Array(i).Sum
	carry(i+1) := FA_Array(i).Cout
  }
  io.o_f := (carry(3)^carry(4) | (io.in_2===8.U & ~(carry(3)^carry(4))) )//detect overflow by carry(3) XOR carry(4)
  //why????
  io.out := sum.asUInt
}
