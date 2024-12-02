import asyncio
import websockets

async def test_websocket():
    uri = "ws://localhost:8887"
    async with websockets.connect(uri) as websocket:
        await websocket.send("""{
  "type": "availability",
  "avail": 1
}""")
        response = await websocket.recv()
        print(f"Received: {response}")

asyncio.run(test_websocket())
