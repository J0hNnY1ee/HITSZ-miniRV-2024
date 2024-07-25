// package miniRV_single_cycle

// import chisel3._
// import chisel3.util._
// import config.Configs._
// import _root_.circt.stage.ChiselStage
// import Core.DataMem
// import Core.InstMem
// import Core.CPUCore

// class OnboardIO extends Bundle {
//   val sw = Input(UInt(24.W))
//   val button = Input(UInt(5.W))
//   val dig_en = Output(UInt(8.W))
//   val DN_A = Output(Bool())
//   val DN_B = Output(Bool())
//   val DN_C = Output(Bool())
//   val DN_D = Output(Bool())
//   val DN_E = Output(Bool())
//   val DN_F = Output(Bool())
//   val DN_G = Output(Bool())
//   val DN_DP = Output(Bool())
//   val led = Output(UInt(24.W))
// }

// class Onboard extends Module {
//   val io = IO(new OnboardIO())
//   val bus = Module(new Bridge())
//   val bled = Module(new BoardLed())
//   val bt = Module(new button())
//   val board_sw = Module(new switch_onboard())
//   val dig = Module(new Digital_LEDs())
//   val dm = Module(new DataMem())
//   val im = Module(new InstMem())
//   val cpu = Module(new CPUCore())

//   // cpu
//   cpu.io.inst <> im.io.inst
//   cpu.io.Bus_rdata <> bus.io.rdata_to_cpu

//   // bus cpu
//   bus.io.addr_from_cpu <> cpu.io.Bus_addr
//   bus.io.ctl_from_cpu <> cpu.io.Bus_memctl
//   bus.io.wD_from_cpu <> cpu.io.Bus_wdata

//   // bus dram
//   bus.io.rdata_from_dram <> dm.io.dataLoad

//   // bus sw
//   bus.io.rdata_from_sw <> board_sw.io.rdata

//   // bus btn
//   bus.io.rdata_from_btn <> bt.io.dataOut

//   // im
//   im.io.addr <> cpu.io.inst_addr
  
//   // dm
//   dm.io.addr <> cpu.io.Bus_addr
//   dm.io.dataStore <> bus.io.wD_to_dram
//   dm.io.ctl <> bus.io.ctl_to_dram

//   // led
//   bled.io.addr <> bus.io.addr_to_led
//   bled.io.we <> bus.io.we_to_led
//   bled.io.wD <> bus.io.wD_to_led

//   // bt
//   bt.io.addr <> bus.io.addr_to_btn
//   bt.io.button_input <> io.button

//   // sw
//   board_sw.io.addr <> bus.io.addr_to_sw
//   board_sw.io.sw <> io.sw

//   // dig
//   dig.io.addr <> bus.io.addr_to_dig
//   dig.io.we <> bus.io.we_to_dig
//   dig.io.wD <> bus.io.wD_to_dig
//   // onBoard
//   io.led <> bled.io.led
//   io.DN_A <> dig.io.dN.DN_A
//   io.DN_B <> dig.io.dN.DN_B
//   io.DN_C <> dig.io.dN.DN_C
//   io.DN_D <> dig.io.dN.DN_D
//   io.DN_E <> dig.io.dN.DN_E
//   io.DN_F <> dig.io.dN.DN_F
//   io.DN_G <> dig.io.dN.DN_G
//   io.DN_DP <> dig.io.dN.DN_DP
//   io.dig_en <> dig.io.dig_en
// }

// object myOb extends App {

//   ChiselStage.emitSystemVerilogFile(
//     new Onboard,
//     firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
//   )

// }
