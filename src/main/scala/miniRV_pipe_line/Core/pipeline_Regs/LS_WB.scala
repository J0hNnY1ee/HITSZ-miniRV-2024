package miniRV_pipe_line.Core.pipeline_Regs

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import miniRV_pipe_line.utils._

class LS_WB_IO extends Bundle {

  val stall = Input(Bool())
  val flush = Input(Bool())

  val rdNum_ls = Input(UInt(REG_NUMS_LOG.W))
  val isRegWrite_ls = Input(Bool())
  val wD_ls = Input(UInt(DATA_WIDTH.W))
  val commit_ls = Input(Bool())

  val rdNum_wb = Output(UInt(REG_NUMS_LOG.W))
  val isRegWrite_wb = Output(Bool())
  val commit_wb = Output(Bool())
  val wD_wb = Output(UInt(DATA_WIDTH.W))
}

class LS_WB extends Module {
  val io = IO(new LS_WB_IO())

  val rdNum_wb = RegInit(0.U(REG_NUMS_LOG.W))
  val isRegWrite_wb = RegInit(0.B)
  val wD_wb = RegInit(0.U(DATA_WIDTH.W))
  val commit_wb = RegInit(0.U)

  when(io.flush) {
    // pc_wb := 0.U
    rdNum_wb := 0.U
    isRegWrite_wb := 0.U
    commit_wb := 0.B
    wD_wb := 0.U
  }.elsewhen(!io.stall) {
    rdNum_wb := io.rdNum_ls
    isRegWrite_wb := io.isRegWrite_ls
    commit_wb := io.commit_ls
    wD_wb := io.wD_ls
  }
  io.rdNum_wb := rdNum_wb
  io.isRegWrite_wb := isRegWrite_wb
  io.commit_wb := commit_wb
  io.wD_wb := wD_wb

}
object myLS_WB extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new LS_WB,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
