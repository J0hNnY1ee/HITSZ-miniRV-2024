package miniRV_pipe_line.Core.pipeline_Regs

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

class IF_ID_IO extends Bundle {
  val stall = Input(Bool())
  val flush = Input(Bool())

  val pc_if = Input(UInt(ADDR_WIDTH.W))
  val inst_if = Input(UInt(DATA_WIDTH.W))
  val commit_if = Input(Bool())

  val pc_id = Output(UInt(ADDR_WIDTH.W))
  val inst_id = Output(UInt(DATA_WIDTH.W))
  val commit_id = Output(Bool())
}

class IF_ID extends Module {
  val io = IO(new IF_ID_IO)
  val pc_id = RegInit(START_ADDR.U(ADDR_WIDTH.W))
  val inst_id = RegInit(0x13.U(DATA_WIDTH.W))
  val commit_id = RegInit(false.B)

  when(io.flush) {
    pc_id := START_ADDR.U
    inst_id := 0x13.U
    commit_id := false.B
  }.elsewhen(!io.stall) {
    pc_id := io.pc_if
    inst_id := io.inst_if
    commit_id := io.commit_if
  }

  io.pc_id := pc_id
  io.commit_id := commit_id
  io.inst_id := inst_id
}

object myIF_ID extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new IF_ID ,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}