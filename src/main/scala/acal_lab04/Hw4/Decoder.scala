package acal_lab04.Hw4

import chisel3._
import chisel3.util._

object opcode_map {
    val LOAD      = 0.U
    val STORE     = 0.U
    val BRANCH    = 0.U
    val JALR      = 0.U
    val JAL       = 0.U
    val OP_IMM    = "b0010011".U
    val OP        = 0.U
    val AUIPC     = 0.U
    val LUI       = 0.U
}

import opcode_map._

class Decoder extends Module{
    val io = IO(new Bundle{
        val inst = Input(UInt(32.W))

        //Please fill in the blanks by yourself
        val funct3 = Output(UInt(3.W))
        val rs1 = Output(UInt(5.W))
        //Please fill in the blanks by yourself
        val rd = Output(UInt(5.W))
        val opcode = Output(UInt(7.W))
        val imm = Output(SInt(32.W))

        // val ctrl_RegWEn = Output(Bool()) for Reg write back
        // val ctrl_ASel = Output(Bool()) for alu src1
        // val ctrl_BSel = Output(Bool()) for alu src2
        // val ctrl_Br = Output(Bool()) for branch inst.
        // val ctrl_Jmp = Output(Bool()) for jump inst.
        // val ctrl_Lui = Output(Bool()) for lui inst.
        // val ctrl_MemRW = Output(Bool()) for L/S inst
        val ctrl_WBSel = Output(Bool())
    })

    //Please fill in the blanks by yourself
    io.funct3 := io.inst(14,12)
    io.rs1 := io.inst(19,15)
    //Please fill in the blanks by yourself
    io.rd := io.inst(11,7)
    io.opcode := io.inst(6,0)

    //ImmGen
    io.imm := MuxLookup(io.opcode,0.S,Seq(
        //R-type
        //Please fill in the blanks by yourself

        //I-type
        OP_IMM -> io.inst(31,20).asSInt
        //Please fill in the blanks by yourself
        //Please fill in the blanks by yourself

        //B-type
        //Please fill in the blanks by yourself

        //S-type
        //Please fill in the blanks by yourself

        //U-type
        //Please fill in the blanks by yourself
        //Please fill in the blanks by yourself

        //J-type
        //Please fill in the blanks by yourself

    ))

    //Controller
    // io.ctrl_RegWEn := ???
    // io.ctrl_ASel := ???
    // io.ctrl_BSel := ???
    // io.ctrl_Br := ???
    // io.ctrl_Jmp := ???
    // io.ctrl_Lui := ???
    // io.ctrl_MemRW := ???
    io.ctrl_WBSel := true.B //true: from alu , false: from dm , another source?
}