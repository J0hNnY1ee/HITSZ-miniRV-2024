package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import config.Configs.ADDR_WIDTH
import config.Configs.DATA_WIDTH
import miniRV_pipe_line._
import miniRV_pipe_line.Core.pipeline_Regs._
import miniRV_pipe_line.utils._

class CoreIO extends Bundle {
  val inst_addr = Output(UInt(ADDR_WIDTH.W))
  val inst = Input(UInt(DATA_WIDTH.W))

  val dataRead = Input(UInt(32.W))
  val dataAddr = Output(UInt(DATA_WIDTH.W)) // peripherals use same addr
  val Dramctl = Flipped(new DataMemControl()) // use them control dmem
  val dataWrite = Output(UInt(DATA_WIDTH.W))
}

class Core extends Module {
  val io = IO(new CoreIO)
  val pc = Module(new PCReg())
//   val im = Module(new InstMem())
  val de = Module(new Decoder())
  val rf = Module(new RegFile())
  val alu = Module(new Alu())
//   val dm = Module(new DataMem())
  // val ctl = Module(new Controller())
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
//   im.io.addr <> pc.io.pc

  // if_id
  if_id.io.commit_if <> rst_n
  if_id.io.pc_if <> pc.io.pc
  if_id.io.inst_if <> io.inst
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
  id_ex.io.rR1_id <> Mux(
    hazard.io.rs1_we,
    hazard.io.rs1_forward,
    rf.io.rR1
  ) // forward
  id_ex.io.rR2_id <> Mux(hazard.io.rs2_we, hazard.io.rs2_forward, rf.io.rR2)
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

  // rf_wsel_ex
  rf_wsel.io.ctl.isLui <> id_ex.io.ctl_ex.isLui
  rf_wsel.io.ctl.isJump <> id_ex.io.ctl_ex.isJump
  rf_wsel.io.AluC <> alu.io.resultC
  rf_wsel.io.dataFromSext <> id_ex.io.imm_ex
  rf_wsel.io.pc <> id_ex.io.pc_ex

  // ex_ls
  ex_ls.io.wD_ex <> rf_wsel.io.wD_ex
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
//   dm.io.addr <> ex_ls.io.resultC_ls
//   dm.io.ctl <> ex_ls.io.mem_ctl_ls
//   dm.io.dataStore <> ex_ls.io.dataStore_ls

  // ls_wb
  ls_wb.io.commit_ls <> ex_ls.io.commit_ls
  ls_wb.io.flush <> hazard.io.flush_ls_wb
  ls_wb.io.stall <> hazard.io.stall_ls_wb
  ls_wb.io.isRegWrite_ls <> ex_ls.io.isRegWrite_ls
  ls_wb.io.rdNum_ls <> ex_ls.io.rdNum_ls
  ls_wb.io.wD_ls <> Mux(
    ex_ls.io.mem_ctl_ls.isLoad,
    io.dataRead,
    ex_ls.io.wD_ls
  )
  ls_wb.io.pc_ls <> ex_ls.io.pc_ls

  // hazard
  // data
  hazard.io.isLoad_ex <> id_ex.io.ctl_ex.isLoad
  hazard.io.rd_ex <> id_ex.io.rdNum_ex
  hazard.io.rd_ls <> ex_ls.io.rdNum_ls
  hazard.io.rd_wb <> ls_wb.io.rdNum_wb
  hazard.io.isRegWrite_ex <> id_ex.io.ctl_ex.isRegWrite
  hazard.io.isRegWrite_ls <> ex_ls.io.isRegWrite_ls
  hazard.io.isRegWrite_wb <> ls_wb.io.isRegWrite_wb
  hazard.io.wD_ex <> rf_wsel.io.wD_ex
  hazard.io.wD_ls <> Mux(
    ex_ls.io.mem_ctl_ls.isLoad,
    io.dataRead,
    ex_ls.io.wD_ls
  )
  hazard.io.wD_wb <> ls_wb.io.wD_wb
  hazard.io.rs1_id <> de.io.regNum.rs1
  hazard.io.rs2_id <> de.io.regNum.rs2
  hazard.io.isSext_id <> de.io.ctl.isSext
  hazard.io.isJAL_id <> de.io.ctl.isJAL
  hazard.io.isLui_id <> de.io.ctl.isLui
  hazard.io.isStore_id <> de.io.ctl.isStore
  // branch
  hazard.io.isJump_ex <> id_ex.io.ctl_ex.isJump
  hazard.io.isBranch_ex <> id_ex.io.ctl_ex.isBranch
  hazard.io.resultBr_ex <> alu.io.resultBr
  hazard.io.resultC_ex <> alu.io.resultC

  // core
  io.inst_addr <> pc.io.pc
  io.dataAddr <> ex_ls.io.resultC_ls
  io.Dramctl <> ex_ls.io.mem_ctl_ls
  io.dataWrite <> ex_ls.io.dataStore_ls
}

object mycore_pipe extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Core,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
