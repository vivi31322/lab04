package acal_lab04.Hw4

import chisel3._
import chisel3.util._

class PC extends Module {
    val io = IO(new Bundle{
        val brtaken = Input(Bool())
        val jmptaken =  Input(Bool())
        val offset = Input(UInt(32.W))
        val pc = Output(UInt(32.W))
    })

    val pcReg = RegInit(0.U(32.W)) //初始化 pcReg = 0
    pcReg := Mux((io.brtaken || io.jmptaken),io.offset&(~3.U(32.W)),pcReg+4.U)
    //pc = pc+4 or offset( from ALU.out )
    io.pc := pcReg
}

