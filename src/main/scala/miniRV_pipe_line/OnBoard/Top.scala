package miniRV_pipe_line.OnBoard


import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

import miniRV_pipe_line.Core._
import miniRV_pipe_line.utils._

class Top_Onboard_IO extends Bundle {

  val sw_input = Input(UInt(SWITCH_NUMBER.W)) // switch
  val button_input = Input(UInt(BUTTON_NUMBER.W))

  val smdled = Output(UInt(LED_NUMBER.W)) // smdled

  val dig_en = Output(UInt(DIG_LED_NUMBER.W))
  val dig_dn = new DigDN()
}

class Top_Onboard extends Module {

  val io = IO(new Top_Onboard_IO())
  val clkwiz = Module(new cpuclk)
  val clk = Wire(Clock())
  val clk_lock = Wire(Bool())
  val pll_clk = Wire(Clock())
  clkwiz.io.clk_in1 := clock
  clk_lock := clkwiz.io.locked
  pll_clk := clkwiz.io.clk_out1
  clk := (clk_lock && pll_clk.asUInt === 1.U).asClock

  withClock(clk) // use  clk instead clock
  {
    // mem
    val im = Module(new InstMem())
    val dm = Module(new DataMem())
    // cpu
    val cpu = Module(new Core())
    // pd
    val bus = Module(new Bus())
    val sw = Module(new Switch())
    val led = Module(new SmdLed())
    val dig = Module(new DigLed)
    val bt = Module(new FiveButton())

    // im
    im.io.addr <> cpu.io.inst_addr

    // dm
    dm.io.addr <> bus.io.mem_addr
    dm.io.ctl <> bus.io.mem_ctl
    dm.io.dataStore <> bus.io.mem_wD

    // sw
    sw.io.addr <> bus.io.pd_addr
    sw.io.sw <> io.sw_input

    // smdled
    led.io.addr <> bus.io.pd_addr
    led.io.we <> bus.io.pd_we
    led.io.wD <> bus.io.pd_wD

    // dig
    dig.io.addr <> bus.io.pd_addr
    dig.io.we <> bus.io.pd_we
    dig.io.wD <> bus.io.pd_wD

    // bt
    bt.io.addr <> bus.io.pd_addr
    bt.io.button <> io.button_input

    // cpu
    cpu.io.dataRead <> bus.io.cpu_rD
    cpu.io.inst <> im.io.inst

    // bus
    bus.io.cpu_addr <> cpu.io.dataAddr
    bus.io.cpu_ctl <> cpu.io.Dramctl
    bus.io.cpu_wD <> cpu.io.dataWrite
    bus.io.mem_rD <> dm.io.dataLoad
    bus.io.pd_rD := MuxLookup(cpu.io.dataAddr, 0.U)(
      Seq(
        PERI_ADDR_SW -> sw.io.swData,
        PERI_ADDR_BTN -> bt.io.btData,
        0.U -> 0.U
      )
    )

    // top
    io.smdled <> led.io.led
    io.dig_dn <> dig.io.dig_dN
    io.dig_en <> dig.io.dig_en
  }
}

object myTop_Onboard extends App {
  println(
    ChiselStage.emitSystemVerilogFile(
      new Top_Onboard,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
