package acal_lab04.Hw4

import chisel3._
import chisel3.util._

class top extends Module {
    val io = IO(new Bundle{
        val pc_out = Output(UInt(32.W))
        val alu_out = Output(UInt(32.W))
        val rf_wdata_out = Output(UInt(32.W))
        val brtaken_out = Output(Bool())
        val jmptaken_out = Output(Bool())
    })

    
    val pc = Module(new PC())
    val im = Module(new InstMem())
	val dm = Module(new DataMem())
    val dc = Module(new Decoder())
    val rf = Module(new RegFile(2))
    val alu = Module(new ALU())
    val bc = Module(new BranchComp())

    //PC
    pc.io.jmptaken := false.B // Don't modify
    pc.io.brtaken := false.B // Don't modify
    pc.io.offset := 0.U // Don't modify

    //Insruction Memory
    im.io.raddr := pc.io.pc

    //Decoder
    dc.io.inst := im.io.rdata

    //RegFile
    rf.io.waddr := 0.U  // Don't modify
    rf.io.wen := false.B // Don't modify
    rf.io.raddr(0) := dc.io.rs1//輸入 rs1 or rs2 的編號，但未必使用到
    rf.io.raddr(1) := dc.io.rs2
    rf.io.wdata := //decoder.WBSel 決定 wbdata 是 (alu.out) or (pc+4) or (DM讀取出來的資料)
	MuxLookup(dc.io.ctrl_WBSel,(dm.io.rdata).asUInt,Seq(1.U->alu.io.out, 2.U->(pc.io.pc+4.U)))
    
    //ALU
    val rdata_or_zero = WireDefault(0.U(32.W))
    alu.io.src1 := Mux(dc.io.ctrl_ASel,pc.io.pc,rf.io.rdata(0))//決定 ALU 的輸入值 > Asel: 0 = rs1, 1 = pc
    alu.io.src2 := Mux(dc.io.ctrl_BSel,dc.io.imm.asUInt,rf.io.rdata(1))//決定 ALU 的輸入值 > Bsel: 0 = rs2, 1 = imm
    alu.io.funct3 := dc.io.funct3
	alu.io.funct7 := dc.io.funct7
    alu.io.opcode := dc.io.opcode

    //Data Memory
    dm.io.funct3 := dc.io.funct3
	dm.io.raddr := alu.io.out//輸入已經計算完畢的位址，讀取資料用 for LOAD
	dm.io.waddr := alu.io.out//輸入已經計算完畢的位址，寫入資料用 for STROE
	dm.io.wdata := rf.io.rdata(1)//要寫入DM的資料
	dm.io.wen := dc.io.ctrl_MemRW//是否寫入DM

    //Branch Comparator
    bc.io.en := dc.io.ctrl_Br//Branch inst. or not
    bc.io.funct3 := dc.io.funct3
    bc.io.src1 := rf.io.rdata(0)//輸入兩個 reg 的值
    bc.io.src2 := rf.io.rdata(1)

    //Check Ports
    io.pc_out := pc.io.pc //pc
    io.alu_out := alu.io.out //alu計算結果 
    io.rf_wdata_out := rf.io.wdata//write back data
    io.brtaken_out := bc.io.brtaken// branch or not
    io.jmptaken_out := dc.io.ctrl_Jmp// jump or not 
}