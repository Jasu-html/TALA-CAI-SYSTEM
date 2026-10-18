const fs = require('fs');
const path = require('path');

const sampleRate = 22050;
const outputDirectory = path.join(__dirname, '..', 'app', 'src', 'main', 'res', 'raw');

function tone(frequency, duration, volume = 0.35, decay = 5) {
  const count = Math.floor(sampleRate * duration);
  const samples = new Array(count);
  for (let index = 0; index < count; index += 1) {
    samples[index] = volume
      * Math.sin((2 * Math.PI * frequency * index) / sampleRate)
      * Math.exp((-decay * index) / sampleRate);
  }
  return samples;
}

function melody(notes, gap = 0.025) {
  const samples = [];
  for (const [frequency, duration] of notes) {
    samples.push(...tone(frequency, duration));
    samples.push(...new Array(Math.floor(sampleRate * gap)).fill(0));
  }
  return samples;
}

function writeWav(filename, samples) {
  const dataSize = samples.length * 2;
  const header = Buffer.alloc(44);
  header.write('RIFF', 0);
  header.writeUInt32LE(36 + dataSize, 4);
  header.write('WAVE', 8);
  header.write('fmt ', 12);
  header.writeUInt32LE(16, 16);
  header.writeUInt16LE(1, 20);
  header.writeUInt16LE(1, 22);
  header.writeUInt32LE(sampleRate, 24);
  header.writeUInt32LE(sampleRate * 2, 28);
  header.writeUInt16LE(2, 32);
  header.writeUInt16LE(16, 34);
  header.write('data', 36);
  header.writeUInt32LE(dataSize, 40);

  const data = Buffer.alloc(dataSize);
  samples.forEach((sample, index) => {
    const clamped = Math.max(-1, Math.min(1, sample));
    data.writeInt16LE(Math.round(clamped * 32767), index * 2);
  });

  fs.mkdirSync(outputDirectory, { recursive: true });
  fs.writeFileSync(path.join(outputDirectory, filename), Buffer.concat([header, data]));
  console.log(`Generated ${filename}: ${(samples.length / sampleRate).toFixed(2)}s`);
}

writeWav('sfx_tap.wav', tone(1046, 0.055, 0.16, 28));
writeWav('sfx_pickup.wav', melody([[660, 0.09], [880, 0.12]]));
writeWav('sfx_drop.wav', tone(250, 0.18, 0.24, 18));
writeWav('sfx_success.wav', melody([[659, 0.1], [784, 0.12], [988, 0.19]], 0.018));
writeWav('sfx_try_again.wav', melody([[520, 0.11], [392, 0.14]], 0.04));
writeWav('sfx_star.wav', melody([[784, 0.09], [988, 0.09], [1175, 0.18]], 0.02));
writeWav('sfx_celebrate.wav', melody([[523, 0.13], [659, 0.13], [784, 0.14], [1047, 0.3]], 0.035));