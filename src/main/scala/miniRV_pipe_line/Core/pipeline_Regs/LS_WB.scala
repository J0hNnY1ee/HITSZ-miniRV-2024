package miniRV_pipe_line.Core.pipeline_Regs

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import miniRV_pipe_line.utils._

class LS_WB_IO extends Bundle {

  val stall = Input(Bool())
  val flush = Input(Bool())

  val pc_ls = Input(UInt(ADDR_WIDTH.W))
  val rdNum_ls = Input(UInt(REG_NUMS_LOG.W))
  val isRegWrite_ls = Input(Bool())
  val resultC_ls = Input(UInt(DATA_WIDTH.W))
  val imm_ls = Input(UInt(DATA_WIDTH.W))
  val dataLoad_ls = Input(UInt(DATA_WIDTH.W))
  val rf_wsel_ls = new RfWselControl()
  val commit_ls = Input(Bool())

  val pc_wb = Output(UInt(ADDR_WIDTH.W))
  val rdNum_wb = Output(UInt(REG_NUMS_LOG.W))
  val isRegWrite_wb = Output(Bool())
  val resultC_wb = Output(UInt(DATA_WIDTH.W))
  val imm_wb = Output(UInt(DATA_WIDTH.W))
  val dataLoad_wb = Output(UInt(DATA_WIDTH.W))
  val rf_wsel_wb = Flipped(new RfWselControl())
  val commit_wb = Output(Bool())
}

class LS_WB extends Module {
  val io = IO(new LS_WB_IO())

  val pc_wb = RegInit(START_ADDR.U(ADDR_WIDTH.W))
  val rdNum_wb = RegInit(0.U(REG_NUMS_LOG.W))
  val isRegWrite_wb = RegInit(0.B)
  val resultC_wb = RegInit(0.U(DATA_WIDTH.W))
  val imm_wb = RegInit(0.U(DATA_WIDTH.W))
  val dataLoad_wb = RegInit(0.U(DATA_WIDTH.W))
  val isLoad_wb = RegInit(0.U)
  val isJump_wb = RegInit(0.U)
  val isLui_wb = RegInit(0.U)
  val commit_wb = RegInit(0.U)

  when(io.flush) {
    pc_wb := 0.U
    rdNum_wb := 0.U
    isRegWrite_wb := 0.U
    resultC_wb := 0.U
    imm_wb := 0.U
    dataLoad_wb := 0.U
    isLoad_wb := 0.B
    isJump_wb := 0.B
    isLui_wb := 0.B
    commit_wb := 0.B
  }.elsewhen(!io.stall) {
    pc_wb := io.pc_ls
    rdNum_wb := io.rdNum_ls
    isRegWrite_wb := io.isRegWrite_ls
    resultC_wb := io.resultC_ls
    imm_wb := io.imm_ls
    dataLoad_wb := io.dataLoad_ls
    isLoad_wb := io.rf_wsel_ls.isLoad
    isJump_wb := io.rf_wsel_ls.isJump
    isLui_wb := io.rf_wsel_ls.isLui
    commit_wb := io.commit_ls
  }
  io.pc_wb := pc_wb
  io.rdNum_wb := rdNum_wb
  io.isRegWrite_wb := isRegWrite_wb
  io.resultC_wb := resultC_wb
  io.imm_wb := imm_wb
  io.dataLoad_wb := dataLoad_wb
  io.rf_wsel_wb.isLoad := isLoad_wb
  io.rf_wsel_wb.isJump := isJump_wb
  io.rf_wsel_wb.isLui := isLui_wb
  io.commit_wb := commit_wb

}
object myLS_WB extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new LS_WB,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
