package miniRV_pipe_line.Core.pipeline_Regs

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import miniRV_pipe_line.utils._

class EX_LS_IO extends Bundle {

  val stall = Input(Bool())
  val flush = Input(Bool())

  val pc_ex = Input(UInt(ADDR_WIDTH.W))
  val rdNum_ex = Input(UInt(REG_NUMS_LOG.W))
  val isRegWrite_ex = Input(Bool())
  val resultC_ex = Input(UInt(DATA_WIDTH.W))
  val commit_ex = Input(Bool())
  val imm_ex = Input(UInt(DATA_WIDTH.W))
  val dataStore_ex = Input(UInt(DATA_WIDTH.W))
// following two Input are rf_wb_sel sinals
  val isLui_ex = Input(Bool())
  val isJump_ex = Input(Bool())
// following four Input are mem_ctl sinals
  val isSigned_ex = Input(Bool())
  val isLoad_ex = Input(Bool())
  val isStore_ex = Input(Bool())
  val ctrlLSType_ex = Input(UInt(LS_TYPES.LS_TYPE_WIDTH.W))
  val wD_ex = Input(UInt(DATA_WIDTH.W))

  val mem_ctl_ls = Flipped(new DataMemControl())
  val rf_wsel_ls = Flipped(new RfWselControl())
  val isRegWrite_ls = Output(Bool())
  val commit_ls = Output(Bool())
  val resultC_ls = Output(UInt(DATA_WIDTH.W))
  val pc_ls = Output(UInt(ADDR_WIDTH.W))
  val rdNum_ls = Output(UInt(REG_NUMS_LOG.W))
  val imm_ls = Output(UInt(DATA_WIDTH.W))
  val dataStore_ls = Output(UInt(DATA_WIDTH.W))
  val wD_ls = Output(UInt(DATA_WIDTH.W))
}

class EX_LS extends Module {
  val io = IO(new EX_LS_IO())
  val commit_ls = RegInit(false.B)
  val resultC_ls = RegInit(0.U(DATA_WIDTH.W))
  val pc_ls = RegInit(0.U(ADDR_WIDTH.W))
  val isSigned_ls = RegInit(true.B)
  val isLui_ls = RegInit(false.B)
  val isStore_ls = RegInit(false.B)
  val isLoad_ls = RegInit(false.B)
  val isJump_ls = RegInit(false.B)
  val ctrlLSType_ls = RegInit(UInt(LS_TYPES.LS_TYPE_WIDTH.W), LS_TYPES.LS_W)
  val rdNum_ls = RegInit(0.U(REG_NUMS_LOG.W))
  val imm_ls = RegInit(0.U(DATA_WIDTH.W))
  val isRegWrite_ls = RegInit(0.B)
  val dataStore_ls = RegInit(0.U(DATA_WIDTH.W))
  val wD_ls = RegInit(0.U(DATA_WIDTH.W))
  when(io.flush) {
    commit_ls := false.B
    resultC_ls := 0.U
    pc_ls := START_ADDR.U
    isSigned_ls := 1.U
    isLui_ls := 0.U
    isStore_ls := 0.U
    isLoad_ls := 0.U
    isJump_ls := 0.U
    ctrlLSType_ls := LS_TYPES.LS_W
    rdNum_ls := 0.U
    isRegWrite_ls := 0.U
    imm_ls := 0.U
    dataStore_ls := 0.U
    wD_ls := 0.U
  }
    .elsewhen(!io.stall) {
      commit_ls := io.commit_ex
      resultC_ls := io.resultC_ex
      pc_ls := io.pc_ex
      isSigned_ls := io.isSigned_ex
      isLui_ls := io.isLui_ex
      isStore_ls := io.isStore_ex
      isLoad_ls := io.isLoad_ex
      isJump_ls := io.isJump_ex
      ctrlLSType_ls := io.ctrlLSType_ex
      rdNum_ls := io.rdNum_ex
      isRegWrite_ls := io.isRegWrite_ex
      imm_ls := io.imm_ex
      dataStore_ls := io.dataStore_ex
      wD_ls := io.wD_ex
    }
  io.commit_ls := commit_ls
  io.resultC_ls := resultC_ls
  io.pc_ls := pc_ls
  io.rf_wsel_ls.isJump := isJump_ls
  // io.rf_wsel_ls.isLoad := isLoad_ls
  io.rf_wsel_ls.isLui := isLui_ls
  io.mem_ctl_ls.isStore := isStore_ls
  io.mem_ctl_ls.isLoad := isLoad_ls
  io.mem_ctl_ls.isSigned := isSigned_ls
  io.mem_ctl_ls.ctrlLSType := ctrlLSType_ls
  io.rdNum_ls := rdNum_ls
  io.isRegWrite_ls := isRegWrite_ls
  io.imm_ls := imm_ls
  io.dataStore_ls := dataStore_ls
  io.wD_ls := wD_ls
}

object myEX_LS extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new EX_LS,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
