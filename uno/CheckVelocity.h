class CheckVelocity{
private:
  float Velocity;// cm/ms

  float TimeGap;//1ms

  float WheelDistance;

public:
  void Update();

  float GetNowVelocity();

  bool IsMagnet();
};

