package miniRV_single_cycle.Core

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import utils.RfWselControl

class Rf_Wsel extends Module {
  val io = IO(new Bundle {
    // JAL or JALR
    val pc = Input(UInt(ADDR_WIDTH.W))
    // Load
    val dataFromRam = Input(UInt(DATA_WIDTH.W))
    // LUI
    val ctl = new RfWselControl()
    val dataFromSext = Input(UInt(DATA_WIDTH.W))
    // Other
    val AluC = Input(UInt(DATA_WIDTH.W))
    val wD = Output(UInt(DATA_WIDTH.W))
  })
  val wD = WireDefault(0.U(DATA_WIDTH.W))
  when(io.ctl.isJump) {
    wD := io.pc + INST_BYTE_WIDTH.U
  }.elsewhen(io.ctl.isLoad)
  {
    wD := io.dataFromRam
  }.elsewhen(io.ctl.isLui)
  {
    wD := io.dataFromSext
  }.otherwise
  {
    wD := io.AluC
  }
  io.wD := wD
}
object myRf_Wsel extends App {
    println(
    ChiselStage.emitSystemVerilog(
      new Rf_Wsel,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
