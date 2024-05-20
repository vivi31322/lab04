package acal_lab04.Hw4

import chisel3._
import chisel3.util._

object opcode_map {
    val LOAD      = "b0000011".U
    val STORE     = "b0100011".U
    val BRANCH    = "b1100011".U
    val JALR      = "b1100111".U
    val JAL       = "b1101111".U
    val OP_IMM    = "b0010011".U
    val OP        = "b0110011".U
    val AUIPC     = "b0010111".U
    val LUI       = "b0110111".U
}

import opcode_map._

class Decoder extends Module{
    val io = IO(new Bundle{
        val inst = Input(UInt(32.W))
        val funct3 = Output(UInt(3.W))
		val funct7 = Output(UInt(7.W))
        val rs1 = Output(UInt(5.W))
		val rs2 = Output(UInt(5.W))        
        val rd = Output(UInt(5.W))
        val opcode = Output(UInt(7.W))
        val imm = Output(SInt(32.W))
        
        // val ctrl_RegWEn = Output(Bool()) for Reg write back 有沒有要寫入RegFile
        val ctrl_ASel = Output(Bool()) //for alu src1 pc or rs1(0)
        val ctrl_BSel = Output(Bool()) //for alu src2 imm or rs2(0)
        val ctrl_Br = Output(Bool()) //for branch inst.
        val ctrl_Jmp = Output(Bool()) //for jump inst.
        val ctrl_Lui = Output(Bool()) //for lui inst.
        val ctrl_MemRW = Output(Bool()) //for L/S inst
        val ctrl_WBSel = Output(UInt(2.W))//for wbdata
    })

    io.funct3 := io.inst(14,12)
	io.funct7 := io.inst(31,25)
    io.rs1 := io.inst(19,15)
	io.rs2 := io.inst(24,20)
    io.rd := io.inst(11,7)
    io.opcode := io.inst(6,0)

    //ImmGen
    io.imm := MuxLookup(io.opcode,0.S,Seq(
        //R-type
        //I-type
        OP_IMM -> io.inst(31,20).asSInt,
		JALR -> io.inst(31,20).asSInt,
		LOAD -> io.inst(31,20).asSInt,
        //B-type
		BRANCH -> (Cat(io.inst(31),io.inst(7),io.inst(30,25),io.inst(11,8))<<1.U).asSInt,//*2U跟<<1U哪裡不一樣
	
		//S-type
		STORE ->  (Cat(io.inst(31,25),io.inst(11,7))).asSInt,

		//U-type
		LUI -> (io.inst(31,12)<<12.U).asSInt,
		AUIPC -> (io.inst(31,12)<<12.U).asSInt,

		//J-type
		JAL -> (Cat(io.inst(31),io.inst(19,12),io.inst(20),io.inst(30,21))<<1.U).asSInt

    ))

    //能不能不要看opcode這麼多次?
    //Controller
    // io.ctrl_RegWEn := ???
    io.ctrl_ASel := (io.opcode===AUIPC)||(io.opcode===BRANCH)||(io.opcode===JAL)//1=pc, 0=rs1
    io.ctrl_BSel := Mux(io.opcode=/=OP,true.B,false.B)//1=imm, 0=rs2
    io.ctrl_Br := Mux(io.opcode===BRANCH,true.B,false.B)// for branch inst. -> need BranchComp
    io.ctrl_Jmp := Mux((io.opcode===JALR||io.opcode===JAL),true.B,false.B)//for jump inst. -> top.io.jmptaken_out
    io.ctrl_Lui := Mux(io.opcode===LUI,true.B,false.B)//for LUI inst.
    io.ctrl_MemRW := (io.opcode===STORE)//for STORE inst. -> write data to DM or not 
    io.ctrl_WBSel := MuxLookup(io.opcode,1.U(2.W),Seq(LOAD->0.U,JAL->2.U,JALR->2.U)) //true: from alu , false: from dm , another: for pc + 4 
                                                
}