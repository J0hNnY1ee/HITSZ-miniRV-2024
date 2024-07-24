package miniRV_single_cycle

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import utils.TraceDenug
import config.Configs.ADDR_WIDTH
import config.Configs.DATA_WIDTH
import utils.DataMemControl

class CPUCoreIO extends Bundle {

// Interface to IROM
  val inst_addr = Output(UInt(ADDR_WIDTH.W))
  val inst = Input(UInt(DATA_WIDTH.W))
// Interface to Bridge
  val Bus_rdata = Input(UInt(32.W))
  val Bus_addr = Output(UInt(DATA_WIDTH.W))
  val Bus_memctl = Flipped(new DataMemControl())
  val Bus_wdata = Output(UInt(DATA_WIDTH.W))
}
class CPUCore extends Module {
  val io = IO(new CPUCoreIO)
  val pc = Module(new PCReg())
  
  val de = Module(new Decoder())
  val rf = Module(new RegFile())
  val alu = Module(new Alu())
  
  val ctl = Module(new Controller())
  val rf_wsel = Module(new Rf_Wsel())

  // pc
  pc.io.addrTarget <> alu.io.resultC
  pc.io.resultBr <> alu.io.resultBr
  pc.io.isJump <> ctl.io.CSOut.isJump
  pc.io.isBranch <> ctl.io.CSOut.isBranch

  
  // de
  de.io.inst <> io.inst

  // rf
  rf.io.regNum <> de.io.regNum
  rf.io.regFileWe <> ctl.io.CSOut.isRegWrite
  rf.io.wD <> rf_wsel.io.wD

  // rf_wsel
  rf_wsel.io.AluC <> alu.io.resultC
  rf_wsel.io.dataFromRam <> io.Bus_rdata
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

  // io
  io.Bus_addr := alu.io.resultC
  io.Bus_wdata := rf.io.rR2
  io.Bus_memctl := ctl.io.DMCtl
  io.inst_addr := pc.io.pc
}

object mycpu_onboard extends App {
  println(
    ChiselStage.emitSystemVerilogFile(
      new CPUCore,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
