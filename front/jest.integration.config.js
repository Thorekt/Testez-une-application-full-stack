const base = require('./jest.config');

module.exports = {
  ...base,
  coverageDirectory: './coverage/jest/integration',
  coverageThreshold: {
    global: {
      statements: 30
    }
  }
};
