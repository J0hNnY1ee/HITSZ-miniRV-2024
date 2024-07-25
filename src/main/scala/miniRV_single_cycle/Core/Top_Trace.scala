package miniRV_single_cycle.Core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import config.Configs.ADDR_WIDTH
import config.Configs.DATA_WIDTH
import miniRV_single_cycle._

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

  // pc
  pc.io.addrTarget <> alu.io.resultC
  pc.io.resultBr <> alu.io.resultBr
  pc.io.isJump <> ctl.io.CSOut.isJump
  pc.io.isBranch <> ctl.io.CSOut.isBranch

  // im
  im.io.addr <> pc.io.pc

  // dm
  dm.io.addr <> alu.io.resultC
  dm.io.dataStore <> rf.io.rR2
  dm.io.ctl <> ctl.io.DMCtl

  // de
  de.io.inst <> im.io.inst

  // rf
  rf.io.regNum <> de.io.regNum
  rf.io.regFileWe <> ctl.io.CSOut.isRegWrite
  rf.io.wD <> rf_wsel.io.wD

  // rf_wsel
  rf_wsel.io.AluC <> alu.io.resultC
  rf_wsel.io.dataFromRam <> dm.io.dataLoad
  rf_wsel.io.dataFromSext <> de.io.imm
  rf_wsel.io.pc <> pc.io.pc
  rf_wsel.io.ctl <> ctl.io.RfWselCtl
  // alu
  alu.io.aluctl <> ctl.io.AluCtl
  alu.io.rR1 <> rf.io.rR1
  alu.io.rR2 <> rf.io.rR2
  alu.io.imm <> de.io.imm
  alu.io.pc <> pc.io.pc

  // ctl
  ctl.io.CSIn <> de.io.ctl

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
