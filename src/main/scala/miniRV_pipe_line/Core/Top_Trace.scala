package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import config.Configs.ADDR_WIDTH
import config.Configs.DATA_WIDTH
import miniRV_pipe_line._
import miniRV_pipe_line.Core.pipeline_Regs._

class TraceDenug extends Bundle {
  val debug_wb_have_inst = Output(Bool())
  val debug_wb_pc = Output(UInt(ADDR_WIDTH.W))
  val debug_wb_ena = Output(Bool())
  val debug_wb_reg = Output(UInt(5.W))
  val debug_wb_value = Output(UInt(DATA_WIDTH.W))
}
class TraceIO extends Bundle {
  val trace = new TraceDenug()
}
class PipeLineCPUForTrace extends Module {
  val io = IO(new TraceIO)
  val pc = Module(new PCReg())
  val im = Module(new InstMem())
  val de = Module(new Decoder())
  val rf = Module(new RegFile())
  val alu = Module(new Alu())
  val dm = Module(new DataMem())
  val ctl = Module(new Controller())
  val rf_wsel = Module(new Rf_Wsel())
  val if_id = Module(new IF_ID)
  val id_ex = Module(new ID_EX)
  val ex_ls = Module(new EX_LS)
  val ls_wb = Module(new LS_WB)
  val hazard = Module(new Hazard())
  val rst_n = !reset.asBool
  // The following codes are connection
  // pc
  pc.io.addrTarget <> hazard.io.pc_addrTarget
  pc.io.pc_we <> hazard.io.pc_we
  pc.io.stall <> hazard.io.stall_pc

  // im
  im.io.addr <> pc.io.pc

  // if_id
  if_id.io.commit_if <> rst_n
  if_id.io.pc_if <> pc.io.pc
  if_id.io.inst_if <> im.io.inst
  if_id.io.flush <> hazard.io.flush_if_id
  if_id.io.stall <> hazard.io.stall_if_id

  // de
  de.io.inst <> if_id.io.inst_id

  // rf
  rf.io.regNum.rs1 <> de.io.regNum.rs1
  rf.io.regNum.rs2 <> de.io.regNum.rs2
  rf.io.regNum.rd <> ls_wb.io.rdNum_wb
  rf.io.regFileWe <> ls_wb.io.isRegWrite_wb
  rf.io.wD <> ls_wb.io.wD_wb

  // id_ex
  id_ex.io.commit_id <> if_id.io.commit_id
  id_ex.io.ctl_id <> de.io.ctl
  id_ex.io.pc_id <> if_id.io.pc_id
  id_ex.io.imm_id <> de.io.imm
  id_ex.io.rdNum_id <> de.io.regNum.rd
  id_ex.io.rR1_id <> rf.io.rR1
  id_ex.io.rR2_id <> rf.io.rR2
  id_ex.io.flush <> hazard.io.flush_id_ex
  id_ex.io.stall <> hazard.io.stall_id_ex

  // alu
  alu.io.aluctl.isBranch <> id_ex.io.ctl_ex.isBranch
  alu.io.aluctl.isJAL <> id_ex.io.ctl_ex.isJAL
  alu.io.aluctl.isSext <> id_ex.io.ctl_ex.isSext
  alu.io.aluctl.isSigned <> id_ex.io.ctl_ex.isSigned
  alu.io.aluctl.op <> id_ex.io.ctl_ex.op
  alu.io.pc <> id_ex.io.pc_ex
  alu.io.imm <> id_ex.io.imm_ex
  alu.io.rR1 <> id_ex.io.rR1_ex
  alu.io.rR2 <> id_ex.io.rR2_ex

  // ex_ls
  ex_ls.io.ctrlLSType_ex <> id_ex.io.ctl_ex.ctrlLSType
  ex_ls.io.imm_ex <> id_ex.io.imm_ex
  ex_ls.io.isJump_ex <> id_ex.io.ctl_ex.isJump
  ex_ls.io.isLoad_ex <> id_ex.io.ctl_ex.isLoad
  ex_ls.io.isLui_ex <> id_ex.io.ctl_ex.isLui
  ex_ls.io.isRegWrite_ex <> id_ex.io.ctl_ex.isRegWrite
  ex_ls.io.isSigned_ex <> id_ex.io.ctl_ex.isSigned
  ex_ls.io.isStore_ex <> id_ex.io.ctl_ex.isStore
  ex_ls.io.pc_ex <> id_ex.io.pc_ex
  ex_ls.io.rdNum_ex <> id_ex.io.rdNum_ex
  ex_ls.io.resultC_ex <> alu.io.resultC
  ex_ls.io.dataStore_ex <> id_ex.io.rR2_ex
  ex_ls.io.stall <> hazard.io.stall_ex_ls
  ex_ls.io.flush <> hazard.io.flush_ex_ls
  ex_ls.io.commit_ex <> id_ex.io.commit_ex

  // dram
  dm.io.addr <> ex_ls.io.resultC_ls
  dm.io.ctl <> ex_ls.io.mem_ctl_ls
  dm.io.dataStore <> ex_ls.io.dataStore_ls

  // rf_wsel
  rf_wsel.io.ctl <> ex_ls.io.rf_wsel_ls
  rf_wsel.io.AluC <> ex_ls.io.resultC_ls
  // rf_wsel.io.dataFromRam <> dm.io.dataLoad
  rf_wsel.io.dataFromSext <> ex_ls.io.imm_ls
  rf_wsel.io.pc <> ex_ls.io.pc_ls

  // ls_wb
  ls_wb.io.commit_ls <> ex_ls.io.commit_ls
  ls_wb.io.flush <> hazard.io.flush_ls_wb
  ls_wb.io.stall <> hazard.io.stall_ls_wb
  ls_wb.io.isRegWrite_ls <> ex_ls.io.isRegWrite_ls
  ls_wb.io.rdNum_ls <> ex_ls.io.rdNum_ls

  // hazard
  hazard.io.isLoad_ex <> id_ex.io.ctl_ex.isLoad
  hazard.io.rd_ex <> id_ex.rdNum_ex
  hazard.io.rd_ls <> ex_ls.io.rdNum_ls
  hazard.io.rd_wb <> ls_wb.io.rdNum_wb
  hazard.io.isRegWrite_ex <> id_ex.io.ctl_ex.isRegWrite
  hazard.io.isRegWrite_ls <> ex_ls.io.isRegWrite_ls
  hazard.io.isRegWrite_wb <> ls_wb.io.isRegWrite_wb
  // hazard.io.wD_ex <> id
  // hazard.io.wD_ls
  // hazard.io.wD_wb

  // debug sinals
  val wb_have_inst = RegInit(0.B)
  val wb_pc = RegInit(0.U(ADDR_WIDTH.W))
  val wb_ena = RegInit(0.B)
  val wb_reg = RegInit(0.U(5.W))
  val wb_value = RegInit(0.U(DATA_WIDTH.W))

  wb_have_inst := 1.B
  wb_pc := pc.io.pc
  wb_ena := ctl.io.CSOut.isRegWrite
  wb_reg := de.io.regNum.rd
  // wb_value := 

  // debug
  io.trace.debug_wb_have_inst := wb_have_inst
  io.trace.debug_wb_pc := wb_pc
  io.trace.debug_wb_ena := wb_ena
  io.trace.debug_wb_reg := wb_reg
  io.trace.debug_wb_value := wb_value
}

object mycpu extends App {
  println(
    ChiselStage.emitSystemVerilogFile(
      new PipeLineCPUForTrace,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
