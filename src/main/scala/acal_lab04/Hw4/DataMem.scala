package acal_lab04.Hw4

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


object wide {
  val Byte = 0.U
  val Half = 1.U
  val Word = 2.U
  val UByte = 4.U
  val UHalf = 5.U
}

import wide._

class DataMem extends Module {
  val io = IO(new Bundle {
    val funct3 = Input(UInt(32.W))
    val raddr = Input(UInt(10.W))
    val rdata = Output(SInt(32.W))

    val wen   = Input(Bool())
    val waddr = Input(UInt(10.W))
    val wdata = Input(UInt(32.W))
  })

  val memory = Mem(32, UInt(8.W))
  loadMemoryFromFile(memory, "./src/main/resource/DataMem.txt")

  io.rdata := 0.S

  val wa = WireDefault(0.U(10.W)) //address
  val wd = WireDefault(0.U(32.W)) //data

  wa := MuxLookup(io.funct3,0.U(10.W),Seq(
    Byte -> io.waddr,
    Half -> (io.waddr& ~(1.U(10.W))), // 對齊地址，2的倍數 -> 末bit = 0
    Word -> (io.waddr& ~(3.U(10.W))), // 同上，4的倍數 -> bits = 00
  ))

  wd := MuxLookup(io.funct3,0.U,Seq(
    Byte -> io.wdata(7,0), //取 byte
    Half -> io.wdata(15,0), // 取 half word
    Word -> io.wdata,
  ))

  when(io.wen){ //STORE
    when(io.funct3===Byte){
      memory(wa) := wd(7,0)
    }.elsewhen(io.funct3===Half){
      memory(wa) := wd(7,0)//Little-Endian, 以 byte 為單位存入(half: 2 byte)
	  memory(wa+1.U) := wd(15,8)
    }.elsewhen(io.funct3===Word){
      memory(wa) := wd(7,0)//Little-Endian, 以 byte 為單位存入(word: 4 byte)
      memory(wa+1.U) := wd(15,8)
      memory(wa+2.U) := wd(23,16)
      memory(wa+3.U) := wd(31,24)
    }
  }.otherwise{ //LOAD
    io.rdata := MuxLookup(io.funct3,0.S,Seq(
      Byte -> memory(io.raddr).asSInt,
      Half -> Cat(memory((io.raddr& ~(1.U(10.W)))+1.U),
				  memory((io.raddr& ~(1.U(10.W))))).asSInt, 
				  //將地址對齊後(無條件將末碼或是末兩碼變成0)，Cat()    
	  Word -> Cat(memory((io.raddr& ~(3.U(10.W)))+3.U),
				  memory((io.raddr& ~(3.U(10.W)))+2.U),
				  memory((io.raddr& ~(3.U(10.W)))+1.U),
				  memory((io.raddr& ~(3.U(10.W))))).asSInt,
				  //同上
      UByte -> Cat(0.U(24.W),memory(io.raddr)).asSInt, //因為需要UInt extend -> Cat 0 before data
      UHalf -> Cat(0.U(16.W),memory((io.raddr& ~(1.U(10.W)))+1.U),memory((io.raddr& ~(1.U(10.W))))).asSInt 
    ))
  }
}