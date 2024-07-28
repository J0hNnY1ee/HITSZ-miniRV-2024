package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import config.Configs.ADDR_WIDTH
import config.Configs.DATA_WIDTH
import miniRV_pipe_line._

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
class CPUForTrace extends Module {
  val io = IO(new TraceIO)
  val pc = Module(new PCReg())
  val im = Module(new InstMem())
  val de = Module(new Decoder())
  val rf = Module(new RegFile())
  val alu = Module(new Alu())
  val dm = Module(new DataMem())
  val ctl = Module(new Controller())
  val rf_wsel = Module(new Rf_Wsel())

  // sinals

  val wb_have_inst = RegInit(0.B)
  val wb_pc = RegInit(0.U(ADDR_WIDTH.W))
  val wb_ena = RegInit(0.B)
  val wb_reg = RegInit(0.U(5.W))
  val wb_value = RegInit(0.U(DATA_WIDTH.W))

  wb_have_inst := 1.B
  wb_pc := pc.io.pc
  wb_ena := ctl.io.CSOut.isRegWrite
  wb_reg := de.io.regNum.rd
  wb_value := rf_wsel.io.wD

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
      new CPUForTrace,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
